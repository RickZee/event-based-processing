# int-test: Cucumber Integration Test Module

This Maven module contains Cucumber-based integration tests for the event-based Spring Boot service.

## Structure
- `src/test/resources/features/` — Cucumber feature files
- `src/test/java/com/example/processing/inttest/` — Step definitions, runners, Spring config

## Running Tests

1. Ensure the main processing project is built and available as a dependency.
2. From this directory, run:

    mvn test

## Dependencies
- Cucumber (Java, Spring, JUnit Platform Engine)
- Spring Boot Test
- JUnit 5

## Notes
- This module should depend on the main processing project for access to domain classes and repositories.
- All integration tests should be written in Cucumber.
