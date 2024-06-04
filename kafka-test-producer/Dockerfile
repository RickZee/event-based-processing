# Use a base image with Java and Maven installed
FROM maven:3.9.7-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Java project to the container
COPY . .

# Build the Java project using Maven
RUN mvn clean install

# Use a base image with just Java installed for smaller image size
FROM eclipse-temurin:21

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR file from the build stage to the container
COPY --from=build /app/target/kafka-test-producer-1.0-SNAPSHOT-jar-with-dependencies.jar .

# Set the command to run the application
CMD ["java", "-jar", "kafka-test-producer-1.0-SNAPSHOT-jar-with-dependencies.jar"]
