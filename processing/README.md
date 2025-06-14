# processing

Technology and architecture evaluation for API-based even ingestion.

## Project Structure

- **api/**: Contains the API module, including controllers, repositories, and domain models for event processing.
- **int-test/**: Contains integration tests and feature files for end-to-end testing.
- **src/**: Main source code for the processing logic, including Java classes and resources.
- **build.gradle**: Root Gradle build configuration file.
- **settings.gradle**: Gradle settings for multi-module configuration.
- **docker-compose.yml**: Docker Compose file for running services required by the application.
- **gradlew.sh / gradlew.bat**: Gradle wrapper scripts for Unix and Windows environments.

## How to Build and Run

**Build the project:**

```sh
./gradlew build
```

**Run tests:**

```sh
./gradlew test
```

**Run with Docker Compose (if applicable):**

```sh
docker-compose up
```

## Requirements

- Java 17 or later
- Gradle (wrapper included)
- Docker (optional, for integration with external services)

## Notes

- The `api` module exposes REST endpoints for event processing.
- The `int-test` module uses feature files for behavior-driven testing.
- Use the Gradle wrapper scripts (`gradlew.sh` or `gradlew.bat`) to ensure consistent builds across environments.

---

For more details, see the documentation in each module or contact the project maintainers.
