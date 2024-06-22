from confluent_kafka import Consumer
from pyflink.dataset import ExecutionEnvironment
from pyflink.table import TableConfig
from pyflink.java_gateway import get_gateway

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
    gateway = get_gateway()
    string_class = gateway.jvm.String
    string_array = gateway.new_array(string_class, 0)
    stream_env = gateway.jvm.org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
    j_stream_exection_environment = stream_env.createRemoteEnvironment(
        "localhost", 
        8081, 
        string_array
    )

    env = StreamExecutionEnvironment(j_stream_exection_environment)


    # Create a streaming TableEnvironment
    exec_env = ExecutionEnvironment.get_execution_environment()
    t_config = TableConfig()

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
