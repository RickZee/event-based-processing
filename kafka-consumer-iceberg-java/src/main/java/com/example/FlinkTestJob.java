package com.example;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.InputStream;
import java.util.Collections;
import java.util.Properties;

import java.util.logging.Logger;

public class FlinkTestJob {
    private static final Logger logger = Logger.getLogger(KafkaConsumerIceberg.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("Starting Kafka Consumer...");

        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");

        properties.put("auto.commit.interval.ms", "1000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("group.id", "test-consumer-group");

        // Properties properties = new Properties();
        // try (InputStream stream = FlinkTestJob.class.getClassLoader().getResourceAsStream("consumer.properties")) {
        //     properties.load(stream);
        // }
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        String topic = "real-estate.public.assessments";

        logger.info(String.format("Subscribing to topic %s ...", topic));
        consumer.subscribe(Collections.singletonList(topic));

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> testSource = KafkaSource.<String>builder()
            .setProperties(properties)
            .setTopics(topic)
            .setStartingOffsets(OffsetsInitializer.latest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        DataStream<String> testStream = env
            .fromSource(testSource, WatermarkStrategy.noWatermarks(), "test-source");

        testStream.print();

        env.execute("Test Job");
    }
}
