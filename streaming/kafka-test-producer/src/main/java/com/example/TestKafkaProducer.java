package com.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class TestKafkaProducer {
    private static Logger logger = LoggerFactory.getLogger(TestKafkaProducer.class);

    public static void main(String[] args) {
        logger.info("Starting Kafka Producer...");

        Properties props = new Properties();

        props.put("bootstrap.servers", "PLAINTEXT://kafka:29092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        String topic = "real-estate.public.assessments";
        logger.info("About to start sending to topic {} ...", topic);

        // Continuous producer
        while (true) {
            String value = "message-" + ThreadLocalRandom.current().nextInt(100000000);
            String key = UUID.randomUUID().toString();

            logger.info("Sending message key = {}, value = {}", key, value);

            producer.send(new ProducerRecord<>(topic, key, value));

            // Sleep for a bit to simulate a steady stream of messages
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        producer.close();
    }
}