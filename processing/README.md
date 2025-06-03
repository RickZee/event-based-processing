# Getting Started

## Processing API

Test ingestion API to save data into a PostgreSQL.

## Setup

### Create a table in Postgres

Open Postgres UI the password is `postgres`.

Create a new connection to server `postgres`.

Open and create the table and data from `./data/create-table.sql`.

## Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

## int-test: Cucumber Integration Test Module

This Maven module contains Cucumber-based integration tests for the event-based Spring Boot service.

### Structure
- `src/test/resources/features/` — Cucumber feature files
- `src/test/java/com/example/processing/inttest/` — Step definitions, runners, Spring config

### Running Tests

1. Ensure the main processing project is built and available as a dependency.
2. From this directory, run:

    mvn test

### Dependencies
- Cucumber (Java, Spring, JUnit Platform Engine)
- Spring Boot Test
- JUnit 5

### Notes
- This module should depend on the main processing project for access to domain classes and repositories.
- All integration tests should be written in Cucumber.
