# Dockerfile for int-test module
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/int-test-1.0-SNAPSHOT.jar ./
COPY src/test/resources ./src/test/resources

# Run tests by default
CMD ["sh", "-c", "java -jar int-test-1.0-SNAPSHOT.jar"]
