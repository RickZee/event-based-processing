# Use a base image with Java and Maven installed
FROM maven:3.8.4-openjdk-11

# Set the working directory inside the container
WORKDIR /app

# Copy the Java project to the container
COPY . .

# Build the Java project using Maven
RUN mvn clean install

# Copy the application JAR file to the container
COPY target/kafka-test-producer-1.0-SNAPSHOT.jar .

# Set the command to run the application
CMD ["java", "-jar", "kafka-test-producer-1.0-SNAPSHOT.jar"]
