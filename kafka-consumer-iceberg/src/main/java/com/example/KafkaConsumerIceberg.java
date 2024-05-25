package com.example;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class KafkaConsumerIceberg {
    private static Logger logger = LoggerFactory.getLogger(KafkaConsumerIceberg.class);
    public static void main(String[] args) {
        logger.info("Starting Kafka Consumer...");

        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");

        // props.put(listeners=PLAINTEXT://0.0.0.0:9092
        // props.put(advertised.listeners=PLAINTEXT://10.75.1.247:9092

        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        String topic = "dbserver1.public.assessments";
        logger.info("Subscribing to topic {} ...", topic);
        consumer.subscribe(Collections.singletonList("dbserver1.public.assessments"));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            }
        }
    }
}
