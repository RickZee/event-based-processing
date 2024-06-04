package com.example;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
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

        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        String topic = "real-estate.public.assessments";

        logger.info("Subscribing to topic {} ...", topic);
        consumer.subscribe(Collections.singletonList(topic));

        // Testing Kafka Consumer
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("Receiving message offset = {}, key = {}, value = {}", record.offset(), record.key(), record.value());
            }
        }

        // DataStream<org.apache.avro.generic.GenericRecord> dataStream = ...;

        // Schema icebergSchema = table.schema();
        
        // // The Avro schema converted from Iceberg schema can't be used
        // // due to precision difference between how Iceberg schema (micro)
        // // and Flink AvroToRowDataConverters (milli) deal with time type.
        // // Instead, use the Avro schema defined directly.
        // // See AvroGenericRecordToRowDataMapper Javadoc for more details.
        // org.apache.avro.Schema avroSchema = AvroSchemaUtil.convert(icebergSchema, table.name());
        
        // GenericRecordAvroTypeInfo avroTypeInfo = new GenericRecordAvroTypeInfo(avroSchema);
        // RowType rowType = FlinkSchemaUtil.convert(icebergSchema);
        
        // FlinkSink.builderFor(
        //     dataStream,
        //     AvroGenericRecordToRowDataMapper.forAvroSchema(avroSchema),
        //     FlinkCompatibilityUtil.toTypeInfo(rowType))
        //   .table(table)
        //   .tableLoader(tableLoader)
        //   .append();
    }
}
