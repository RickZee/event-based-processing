# Copilot Instructions for int-test Integration Test Module

- This module contains Cucumber-based integration tests for the event-based Spring Boot service.
- All Cucumber feature files, step definitions, runners, and Spring test configuration should reside here.
- The module must depend on the main processing project to access domain classes and repositories.
- Use JUnit 5, Cucumber, and Spring Boot Test for all integration tests.
- Feature files should be placed under `src/test/resources/features`.
- Step definitions, runners, and config should be under `src/test/java/com/example/processing/inttest/`.
- To run tests: `mvn test` from this module root.
