# Event-Based Processing Monorepo

This repository contains a collection of projects and modules for event-based data ingestion, processing, streaming, and integration testing. It supports both Java (Spring Boot, Gradle, Maven) and Python (for streaming/ETL), and includes Docker Compose setups for local development.

## Repository Structure

```terminal
.
├── java-numerics/                    # Java numerics, MongoDB, Kafka, Avro, and serialization tests
├── processing/                       # Main event processing system (Gradle, Spring Boot)
│   ├── api/                          # API module (Spring Boot, JPA, REST)
│   ├── int-test/                     # Integration tests (Cucumber, Spring Boot)
│   ├── data/                         # SQL scripts and data files
│   ├── k8s/                          # Kubernetes manifests
│   ├── docker-compose.yml            # Docker Compose for local dev
│   └── ...                           # Gradle wrapper, settings, etc.
├── streaming/                        # Batch/streaming data ingestion (Flink, Python, Kafka, Debezium)
│   ├── kafka-consumer-iceberg-java/  # Java consumer for Kafka to Apache Iceberg
│   ├── kafka-s3-integration-test/    # Integration tests for Kafka and S3
│   ├── kafka-test-producer/          # Kafka test producer
│   ├── data/                         # Scripts for connectors, Minio, etc.
│   ├── doc/                          # Documentation and diagrams
│   ├── k8s/                          # Kubernetes manifests for streaming stack
│   └── docker-compose.yml            # Docker Compose for streaming stack
└── ...                               # Repo description, git things etc.
```

## Subfolder Highlights

### `processing/`

- **Spring Boot, Gradle-based event processing API**
- REST endpoints, JPA, PostgreSQL/H2 support
- Integration tests with Cucumber and H2/Postgres compatibility
- Docker Compose and Kubernetes manifests for local and cloud deployment

### `streaming/`

- **Flink, Kafka, Debezium, Minio, Postgres, Python, S3, Iceberg, and test producers**
- Batch and streaming data pipelines
- Scripts for connector setup and ETL automation
- UI dashboards for Flink, Kafka, Minio, Postgres
- Java-based connectors and integration test suites

### `java-numerics/`

- **Java numerics, MongoDB, Kafka, Avro, JSON serialization**
- Test utilities and permutation generators for numeric types

## Requirements

- Java 17+ (see `build.gradle`/`pom.xml`)
- Python 3.11 (for streaming, not 3.12+)
- Docker & Docker Compose
- Maven (for legacy modules)
- Gradle (for main processing module)

## Notes

- See each subfolder's `README.md` for more details and module-specific instructions.
