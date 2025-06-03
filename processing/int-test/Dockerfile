# Dockerfile for int-test module
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*.jar ./
COPY src/test/resources ./src/test/resources

# Run tests by default
CMD ["sh", "-c", "java -jar $(ls int-test-*.jar | head -n1)"]
