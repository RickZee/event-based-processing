from confluent_kafka import Consumer

if __name__ == '__main__':

    # Consumer configuration
    config = {
        'bootstrap.servers': 'localhost:9092',
        'auto.commit.interval.ms': 1000,
        'group.id': 'iceberg-consumer-group',
        'session.timeout.ms': 6000,
        'default.topic.config': {'auto.offset.reset': 'smallest'},
    }

    # Create Consumer instance
    consumer = Consumer(config)

    # Subscribe to topic
    topic = "real-estate.public.assessments"
    consumer.subscribe([topic])

    # Poll for new messages from Kafka and print them.
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                # Initial message consumption may take up to
                # `session.timeout.ms` for the consumer group to
                # rebalance and start consuming
                print("Waiting...")
            elif msg.error():
                print("ERROR: %s".format(msg.error()))
            else:
                msg_key = msg.key()
                msg_value = msg.value()
                # Extract the (optional) key and value, and print.
                print("Consumed event from topic {topic}, value {msg_value}".format(
                    topic=msg.topic(), msg_value=msg_value))
                                
                # print("Consumed event from topic {topic}: key = {key:12} value = {value:12}".format(
                #     topic=msg.topic(), key=msg.key().decode('utf-8'), value=msg.value().decode('utf-8')))
    except KeyboardInterrupt:
        pass
    finally:
        # Leave group and commit final offsets
        consumer.close()


# from kafka import Consumer, KafkaError
# import logging

# # Setting up logging
# logging.basicConfig(level=logging.INFO)
# logger = logging.getLogger("KafkaConsumerIceberg")

# def main():
#     logger.info("Starting Kafka Consumer...")

#     # Consumer configuration
#     conf = {
#         'bootstrap.servers': 'localhost:9092',
#         'auto.commit.interval.ms': 1000,
#         'key.deserializer': 'org.apache.kafka.common.serialization.StringDeserializer',
#         'value.deserializer': 'org.apache.kafka.common.serialization.StringDeserializer',
#         'group.id': 'iceberg-consumer-group',
#         'session.timeout.ms': 6000,
#         'default.topic.config': {'auto.offset.reset': 'smallest'}
#     }

#     # Create Consumer instance
#     consumer = Consumer(**conf)
#     topic = "real-estate.public.assessments"

#     logger.info(f"Subscribing to topic {topic} ...")
#     consumer.subscribe([topic])

#     # Testing Kafka Consumer
#     test_consume_from_kafka(consumer)

#     # Placeholder for streamIntoIcebergFromKafka function
#     # try:
#     #     stream_into_iceberg_from_kafka(consumer)
#     # except Exception as e:
#     #     logger.error(e, exc_info=True)
#     #     exit(1)

# def test_consume_from_kafka(consumer):
#     try:
#         while True:
#             msg = consumer.poll(1.0)  # Poll for messages

#             if msg is None:
#                 continue
#             if msg.error():
#                 if msg.error().code() == KafkaError._PARTITION_EOF:
#                     # End of partition event
#                     logger.info('%% %s [%d] reached end at offset %d\n' %
#                                 (msg.topic(), msg.partition(), msg.offset()))
#                 elif msg.error():
#                     raise KafkaException(msg.error())
#             else:
#                 # Message is a normal message
#                 logger.info('Received message: {}'.format(msg.value().decode('utf-8')))
#     finally:
#         # Clean up on exit
#         consumer.close()

# if __name__ == "__main__":
#     main()


# from pyflink.common import Types
# from pyflink.datastream import StreamExecutionEnvironment
# from pyflink.table import StreamTableEnvironment

# # Create a StreamExecutionEnvironment
# env = StreamExecutionEnvironment.get_execution_environment()

# # Create a StreamTableEnvironment
# t_env = StreamTableEnvironment.create(env)

# # Define the Kafka source
# source = t_env.from_kafka(
#     "kafka_topic",
#     key_type=Types.STRING(),
#     value_type=Types.ROW([Types.STRING(), Types.INT()]),
# )

# # Define the Iceberg sink
# sink = t_env.to_iceberg(
#     "iceberg_table",
#     format="parquet",
#     partitioning=["key"],
# )

# # Connect the source and sink
# source.insert_into(sink)

# # Execute the job
# env.execute("Kafka to Iceberg")
