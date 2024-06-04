package com.example;

// import java.util.List;
// import java.util.stream.Collectors;
// import org.apache.flink.api.common.typeinfo.TypeInformation;
// import org.apache.flink.api.java.typeutils.RowTypeInfo;

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import static org.apache.flink.table.api.Expressions.$;

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

    public static void main(String[] args) throws Exception {
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
        // testConsumeFromKafka(consumer);

        try {
            streamIntoIcebergFromKafka(consumer);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        // Stream into Iceberg using Flink

    }

    private static void streamIntoIcebergFromKafka(KafkaConsumer<String, String> consumer) throws Exception {

        // set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // ...

        final StreamTableEnvironment tableEnv = StreamTableEnvironment.create(
                env,
                EnvironmentSettings.newInstance().inStreamingMode().build());

        // List all catalogs
        TableResult result = tableEnv.executeSql("SHOW CATALOGS");

        // Print the result to standard out
        result.print();

        // Set the current catalog to the new catalog
        tableEnv.useCatalog("iceberg");

        // Create a database in the current catalog
        tableEnv.executeSql("CREATE DATABASE IF NOT EXISTS db");

        // create the table
        tableEnv.executeSql(
                "CREATE TABLE IF NOT EXISTS db.table1 ("
                        + "id BIGINT COMMENT 'unique id',"
                        + "data STRING"
                        + ")");

        // create a DataStream of Tuple2 (equivalent to Row of 2 fields)
        DataStream<Tuple2<Long, String>> dataStream = env.fromElements(
                Tuple2.of(1L, "foo"),
                Tuple2.of(1L, "bar"),
                Tuple2.of(1L, "baz"));

        // convert the DataStream to a Table
        Table table = tableEnv.fromDataStream(dataStream, $("id"), $("data"));

        // register the Table as a temporary view
        tableEnv.createTemporaryView("my_datastream", table);

        // write the DataStream to the table
        tableEnv.executeSql(
                "INSERT INTO db.table1 SELECT * FROM my_datastream");

        env.execute("Flink Streaming Java API Skeleton");
    }

    private static void testConsumeFromKafka(KafkaConsumer<String, String> consumer) {
        // Testing Kafka Consumer
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("Receiving message offset = {}, key = {}, value = {}", record.offset(), record.key(),
                        record.value());
            }
        }
    }
}
