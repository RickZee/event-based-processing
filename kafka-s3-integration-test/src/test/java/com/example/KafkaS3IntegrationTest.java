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
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
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

import org.apache.avro.file.DataFileReader;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
        // Initialize S3 client for LocalStack
        s3Client = S3Client.builder()
                .endpointOverride(URI.create("http://localhost:4566"))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
                .region(Region.US_EAST_1)
                .build();

        // Create S3 bucket
        s3Client.createBucket(b -> b.bucket(BUCKET_NAME));

        // Register Avro schema
        String schemaContent = new String(
                Files.readAllBytes(new File("src/test/resources/decimal_record.avsc").toPath()));
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
        if (s3Client != null)
            s3Client.close();
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
                "us-east-1");

        // Produce messages with BigDecimal
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
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
            byte[] avroBytes = objectStream.readAllBytes();
            DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);
            try (DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(
                    new org.apache.avro.file.SeekableByteArrayInput(avroBytes), datumReader)) {
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