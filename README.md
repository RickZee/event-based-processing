# event-based-processing

Event based ingestion and processing service to handle batch or streaming data.

## Prerequisites

Python 3.11 or *below*. Python 3.12 removed some setup utilities required by flink packages.

## Running in docker-compose

### Contains the following services

* flink-taskmanager
* flink-jobmanager
* kafka-test-producer
* debezium
* kafka-ui
* kafka
* pgadmin
* postgres
* zookeeper

### UI and dashboard links

* Postgres UI: <http://localhost:8082/>
* Flink dashboard: <http://localhost:8081/>
* Minio dashboard UI: <http://localhost:9001/>
* Kafka UI: <http://localhost:8080/>

## Setup

### Build and run services

Run `docker-compose build && docker-compose up`.

### Add Debezium connector

Add Debezium connector from `./data/add-connector.sh` (you can just copy-paste the contents in your terminal).

### Create a table in Postgres

Open Postgres UI the password is `postgres`.

Create a new connection to server `postgres`.

Open and create the table and data from `./data/create-assessment-table.sql`.

### Create a bucket in Minio

```shell
cd data
python3 -m venv .venv-data
source .venv-data/bin/activate
pip install -r requirements.txt
python make_minio_bucket.py
```

## Types of ingestion

### Batch processing using Flink and Kafka

[TBD]

### Stream processing using Flink and Kafka

Run Kafka consumer from `kafka-consumer-iceberg` folder.

Open Postgres UI, go to `real-estate` database, `assessments` table.

Start making changes to the `assessments` table. Watch Debezium (CDC) piping the data into Kafka.

## Kafka to Iceberg Consumer

```shell
cd kafka-consumer-iceberg-python
python3 -m venv .venv-producer
source .venv-producer/bin/activate
pip install -r requirements.txt
python make_minio_bucket.py
```

## Running in k8s

[Follow the instructions in k8s/README.md](./k8s/README.md)

## Cleanup and Troubleshooting

Sometimes you need to delete docker volumes with the containers:

`docker-compose down -v`

This will clear all data stored and settings, so you'll have to restart from scratch.

If that doesn't help you may have to remove docker data or reset your docker desktop.
