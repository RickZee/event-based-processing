package com.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class KafkaConnectHelper {
    private final String connectUrl;
    private final HttpClient httpClient;

    public KafkaConnectHelper(String connectUrl) {
        this.connectUrl = connectUrl;
        this.httpClient = HttpClient.newHttpClient();
    }

    public void createS3SinkConnector(String connectorName, String topic, String schemaRegistryUrl,
                                     String s3BucketName, String s3Endpoint, String s3Region) throws IOException, InterruptedException {
        Map<String, String> config = new HashMap<>();
        config.put("connector.class", "io.confluent.connect.s3.S3SinkConnector");
        config.put("tasks.max", "1");
        config.put("topics", topic);
        config.put("format.class", "io.confluent.connect.s3.format.avro.AvroFormat");
        config.put("flush.size", "1"); // Small flush size for testing
        config.put("schema.compatibility", "NONE");
        config.put("storage.class", "io.confluent.connect.s3.storage.S3Storage");
        config.put("s3.bucket.name", s3BucketName);
        config.put("s3.region", s3Region);
        config.put("s3.endpoint", s3Endpoint);
        config.put("s3.credentials.provider.class", "com.amazonaws.auth.DefaultAWSCredentialsProviderChain");
        config.put("value.converter", "io.confluent.connect.avro.AvroConverter");
        config.put("value.converter.schema.registry.url", schemaRegistryUrl);
        config.put("key.converter", "org.apache.kafka.connect.storage.StringConverter");

        String jsonConfig = String.format("{\"name\":\"%s\",\"config\":%s}",
                connectorName, toJsonString(config));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(connectUrl + "/connectors"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonConfig))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Failed to create connector: " + response.body());
        }
    }

    private String toJsonString(Map<String, String> map) {
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> sb.append(String.format("\"%s\":\"%s\",", k, v)));
        sb.setLength(sb.length() - 1); // Remove trailing comma
        sb.append("}");
        return sb.toString();
    }
}