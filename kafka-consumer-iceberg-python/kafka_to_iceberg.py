from confluent_kafka import Consumer
# from pyflink.common import Types
# from pyflink.datastream import StreamExecutionEnvironment

from pyflink.table import *
from pyflink.table.expressions import col
from pyflink.table import EnvironmentSettings, TableEnvironment

def main():
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

    # test_consumer(consumer)

    stream_to_iceberg(topic)

def stream_to_iceberg(topic):
    # Create a streaming TableEnvironment
    env_settings = EnvironmentSettings.in_streaming_mode()
    table_env = TableEnvironment.create(env_settings)
    table_env

    # # Create a StreamExecutionEnvironment
    # env = StreamExecutionEnvironment.get_execution_environment()

    # # Create a StreamTableEnvironment
    # table_env = StreamTableEnvironment.create(env)

    # # Define the Kafka source
    # source = table_env.from_kafka(
    #     topic,
    #     key_type=Types.STRING(),
    #     value_type=Types.STRING(),
    # )

    # # Define the Iceberg sink
    # sink = table_env.to_iceberg(
    #     "assessments",
    #     format="parquet",
    #     partitioning=["key"],
    # )

    # # Connect the source and sink
    # source.insert_into(sink)

    # # Execute the job
    # env.execute("Kafka to Iceberg")

def test_consumer(consumer):
    # Poll for new messages from Kafka and print them.
    try:
        while True:
            msg = consumer.poll(1.0)
            if msg is None:
                # Initial message consumption may take up to
                # `session.timeout.ms` for the consumer group to
                # re-balance and start consuming
                print("Waiting...")
            elif msg.error():
                print("ERROR: %s".format(msg.error()))
            else:
                msg_key = msg.key()
                msg_value = msg.value()
                # Extract the (optional) key and value, and print.
                print("Consumed event from topic {topic}, value {msg_value}".format(
                    topic=msg.topic(), msg_value=msg_value))
    except KeyboardInterrupt:
        pass
    finally:
        # Leave group and commit final offsets
        consumer.close()

if __name__ == "__main__":
    main()
