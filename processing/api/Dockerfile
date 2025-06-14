# Dockerfile for processing module
FROM eclipse-temurin:17-jre

ENV DB_URL=
ENV DB_USERNAME=
ENV DB_PASSWORD=

WORKDIR /app

COPY build/libs/api-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
