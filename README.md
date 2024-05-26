# kafka-consumer-iceberg

Event based ingestion and processing service to handle batch or streaming data

## Start `docker-compose`

Contains the following services:

* zookeeper
* kafka
* postgres
* debezium
* kafka-ui
* pgadmin
* schema-registry

Add Debezium connector by running `add-connector.sh`.

## Let's try to use the following examples

### Batch processing using Flink and Kafka

[TBD]

### Stream processing using Flink and Kafka

Run Kafka consumer from `kafka-consumer-iceberg` folder.

Kafka UI is available at <http://localhost:8080/>

Open Postgres DB UI (available on <http://localhost:8082/>), go to `real-estate` database, `assessments` table.

Start making changes to the `assessments` table. Watch Debezium (CDC) piping the data into Kafka.

## Cleanup and Troubleshooting

Sometimes you need to delete docker volumes with the containers:

`docker-compose down -v`

This will create all data stored and settings so you'll have to restart from scratch.
