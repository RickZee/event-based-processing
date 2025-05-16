package com.example;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

import java.io.File;
import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testcontainers.containers.localstack.LocalStackContainer.Service.S3;

public class KafkaS3IntegrationTest {
    private static KafkaContainer kafkaContainer;
    private static LocalStackContainer localStackContainer;
    private static GenericContainer schemaRegistryContainer;
    private static GenericContainer kafkaConnectContainer;
    private static S3Client s3Client;
    private static final String TOPIC = "decimal-topic";
    private static final String BUCKET_NAME = "test-bucket";
    private static final String SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final String CONNECT_URL = "http://localhost:8083";

    @BeforeAll
    public static void setUp() throws Exception {
        // Start Kafka with Confluent image
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.7.1"));
        kafkaContainer.start();

        // Start Schema Registry
        schemaRegistryContainer = new GenericContainer(DockerImageName.parse("confluentinc/cp-schema-registry:7.7.1"))
                .withNetwork(kafkaContainer.getNetwork())
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", kafkaContainer.getBootstrapServers())
                .withExposedPorts(8081);
        schemaRegistryContainer.start();

        // Start LocalStack for S3 emulation
        localStackContainer = new LocalStackContainer(DockerImageName.parse("localstack/localstack:3.8.1"))
                .withServices(S3);
        localStackContainer.start();

        // Start Kafka Connect with S3 Sink Connector
        kafkaConnectContainer = new GenericContainer(DockerImageName.parse("confluentinc/cp-kafka-connect:7.7.1"))
                .withNetwork(kafkaContainer.getNetwork())
                .withEnv("CONNECT_BOOTSTRAP_SERVERS", kafkaContainer.getBootstrapServers())
                .withEnv("CONNECT_REST_ADVERTISED_HOST_NAME", "kafka-connect")
                .withEnv("CONNECT_GROUP_ID", "connect-group")
                .withEnv("CONNECT_CONFIG_STORAGE_TOPIC", "connect-configs")
                .withEnv("CONNECT_OFFSET_STORAGE_TOPIC", "connect-offsets")
                .withEnv("CONNECT_STATUS_STORAGE_TOPIC", "connect-status")
                .withEnv("CONNECT_KEY_CONVERTER", "org.apache.kafka.connect.storage.StringConverter")
                .withEnv("CONNECT_VALUE_CONVERTER", "io.confluent.connect.avro.AvroConverter")
                .withEnv("CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL", SCHEMA_REGISTRY_URL)
                .withEnv("CONNECT_PLUGIN_PATH", "/usr/share/java,/usr/share/confluent-hub-components")
                .withExposedPorts(8083)
                .withCommand("sh", "-c", "confluent-hub install --no-prompt confluentinc/kafka-connect-s3:10.5.8 && /etc/confluent/docker/run");
        kafkaConnectContainer.start();

        // Initialize S3 client for LocalStack
        s3Client = S3Client.builder()
                .endpointOverride(localStackContainer.getEndpoint())
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .build();

        // Create S3 bucket
        s3Client.createBucket(b -> b.bucket(BUCKET_NAME));

        // Register Avro schema
        String schemaContent = new String(Files.readAllBytes(new File("src/test/resources/decimal_record.avsc").toPath()));
        HttpClient httpClient = HttpClient.newHttpClient();
        String schemaJson = String.format("{\"schema\":%s}", schemaContent);
        HttpRequest schemaRequest = HttpRequest.newBuilder()
                .uri(URI.create(SCHEMA_REGISTRY_URL + "/subjects/" + TOPIC + "-value/versions"))
                .header("Content-Type", "application/vnd.schemaregistry.v1+json")
                .POST(HttpRequest.BodyPublishers.ofString(schemaJson))
                .build();
        HttpResponse<String> schemaResponse = httpClient.send(schemaRequest, HttpResponse.BodyHandlers.ofString());
        if (schemaResponse.statusCode() != 200) {
            throw new RuntimeException("Failed to register schema: " + schemaResponse.body());
        }
    }

    @AfterAll
    public static void tearDown() {
        if (kafkaConnectContainer != null) kafkaConnectContainer.stop();
        if (schemaRegistryContainer != null) schemaRegistryContainer.stop();
        if (localStackContainer != null) localStackContainer.stop();
        if (kafkaContainer != null) kafkaContainer.stop();
        if (s3Client != null) s3Client.close();
    }

    @Test
    public void testKafkaToS3Streaming() throws Exception {
        // Configure S3 Sink Connector
        KafkaConnectHelper connectHelper = new KafkaConnectHelper(CONNECT_URL);
        connectHelper.createS3SinkConnector(
                "s3-sink",
                TOPIC,
                SCHEMA_REGISTRY_URL,
                BUCKET_NAME,
                localStackContainer.getEndpoint().toString(),
                "us-east-1"
        );

        // Produce messages with BigDecimal
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        producerProps.put("schema.registry.url", SCHEMA_REGISTRY_URL);

        Schema schema = new Schema.Parser().parse(new File("src/test/resources/decimal_record.avsc"));
        BigDecimal[] testValues = {
                new BigDecimal("123.4567890123"),
                new BigDecimal("0.0000000001"),
                BigDecimal.ZERO
        };

        try (KafkaProducer<String, GenericRecord> producer = new KafkaProducer<>(producerProps)) {
            for (BigDecimal value : testValues) {
                GenericRecord record = new GenericData.Record(schema);
                record.put("value", value);
                producer.send(new ProducerRecord<>(TOPIC, null, record)).get();
            }
        }

        // Wait for connector to process messages (adjust based on system)
        Thread.sleep(10000);

        // Verify objects in S3
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(ListObjectsV2Request.builder()
                .bucket(BUCKET_NAME)
                .build());
        List<String> objectKeys = listResponse.contents().stream()
                .map(s3Object -> s3Object.key())
                .collect(Collectors.toList());
        assertFalse(objectKeys.isEmpty(), "No objects found in S3 bucket");

        // Read and verify Avro content
        for (String key : objectKeys) {
            ResponseInputStream<GetObjectResponse> objectStream = s3Client.getObject(GetObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(key)
                    .build());
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(new SeekableInputStream(objectStream), datumReader)) {
                while (dataFileReader.hasNext()) {
                    GenericRecord record = dataFileReader.next();
                    BigDecimal retrievedValue = (BigDecimal) record.get("value");
                    boolean matches = false;
                    for (BigDecimal testValue : testValues) {
                        if (testValue.compareTo(retrievedValue) == 0) {
                            matches = true;
                            break;
                        }
                    }
                    assertTrue(matches, "Retrieved BigDecimal " + retrievedValue + " does not match any test value");
                }
            }
        }
    }
}