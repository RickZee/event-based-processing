# processing

Technology and architecture evaluation for API-based even ingestion.

## Project Structure

- **api/**: Contains the API module, including controllers, repositories, and domain models for event processing.
- **int-test/**: Contains integration tests and feature files for end-to-end testing.
- **src/**: Main source code for the processing logic, including Java classes and resources.
- **settings.gradle**: Gradle settings for multi-module configuration.
- **docker-compose.yml**: Docker Compose file for running services required by the application.
- **gradlew.sh / gradlew.bat**: Gradle wrapper scripts for Unix and Windows environments.

## How to Build and Run

### Build the project

```sh
./gradlew.sh build
```

**Note:** `processing/int-test/src/test/resources/schema.sql` contains SQL dialect for in-mem H2-compatible database. Use `processing/data/create-table.sql` to create a table in real Postgres.

### Run tests

```sh
./gradlew.sh test
```

### Run with Docker Compose (if applicable)

```sh
docker-compose up
```

### Run Cucumber Tests

**With live database:**

```sh
SPRING_PROFILES_ACTIVE=postgres DB_URL=jdbc:postgresql://localhost:5432/api-processing DB_USERNAME=postgres DB_PASSWORD=postgres ./gradlew.sh test -p int-test --tests '*Cucumber*'
```

**With in-memory database:**

```sh
./gradlew.sh test -p int-test --tests '*Cucumber*'
```

### API Environment Variables

```sh
DB_URL=jdbc:postgresql://localhost:5432/api-processing
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

## Requirements

- Java 17 or later
- Gradle (wrapper included)
- Docker (optional, for integration with external services)

## Notes

- The `api` module exposes REST endpoints for event processing.
- The `int-test` module uses feature files for behavior-driven testing.
- Use the Gradle wrapper scripts (`gradlew.sh` or `gradlew.bat`) to ensure consistent builds across environments.
