package com.example;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.formats.json.JsonDeserializationSchema;
import org.apache.flink.formats.json.JsonSerializationSchema;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;
import static org.apache.flink.table.api.Expressions.$;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Properties;

import java.util.logging.Logger;
import java.io.InputStream;

public class KafkaConsumerIceberg {
    private static final Logger logger = Logger.getLogger(KafkaConsumerIceberg.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("Starting Kafka Consumer...");

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");

        // properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "iceberg-consumer-group");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);
        String topic = "real-estate.public.assessments";

        logger.info(String.format("Subscribing to topic %s ...", topic));
        consumer.subscribe(Collections.singletonList(topic));

        // Testing Kafka Consumer
        // testConsumeFromKafka(consumer);

        // Stream into Iceberg using Flink
        try {
            streamIntoIcebergFromKafka(consumer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void streamIntoIcebergFromKafka(KafkaConsumer<String, String> consumer) throws Exception {
        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(
                environment,
                EnvironmentSettings.newInstance().inStreamingMode().build());

        // List all catalogs
        TableResult result = tableEnv.executeSql("SHOW CATALOGS");

        // Print the result to standard out
        logger.info("Catalogs: " + result.toString());

        // Set the current catalog to the new catalog
        logger.info("Setting current catalog to iceberg");
        tableEnv.useCatalog("iceberg");

        // Create a database in the current catalog
        logger.info("Creating database real-estate");
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS real-estate");

        // create the table
        logger.info("Creating table real-estate.assessments");
        tableEnv.executeSql(
                "CREATE TABLE IF NOT EXISTS real-estate.assessments ("
                        + "id BIGINT COMMENT 'unique id',"
                        + "data STRING"
                        + ")");

        environment.execute("Event-based processing");
    }

    private static void testConsumeFromKafka(KafkaConsumer<String, String> consumer) {
        // Testing Kafka Consumer
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                logger.info(String.format("Receiving message offset = %s, key = %s, value = %s", 
                    record.offset(), record.key(), record.value()));
            }
        }
    }
}
