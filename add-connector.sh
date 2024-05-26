#!/bin/bash

# The script below is used to add a connector to the Kafka Connect service. The connector is a Debezium PostgreSQL connector that listens to the PostgreSQL database and sends the changes to the Kafka topic. 
# The script uses the  curl  command to send a POST request to the Kafka Connect service. The request contains the connector configuration in JSON format. 
# The  curl  command sends the request to the  /connectors  endpoint of the Kafka Connect service. The  -X POST  option specifies that the request is a POST request. The  -H 'Content-Type: application/json'  option specifies that the request body is in JSON format. The  -k  option allows the script to send the request to an insecure server. The  -u kc_username:kc_password  option specifies the username and password for the Kafka Connect service. The  -d  option specifies the request body. 
# The request body contains the connector configuration in JSON format. The configuration specifies the connector class, the database hostname, port, user, password, database name, server name, and plugin name. 
# The script is used to add a Debezium PostgreSQL connector to the Kafka Connect service. The connector listens to the PostgreSQL database and sends the changes to the Kafka topic. 
# Conclusion 
# In this article, we have shown you how to use the Debezium PostgreSQL connector to stream changes from a PostgreSQL database to a Kafka topic. We have also shown you how to use the Kafka Connect service to add a connector to the Kafka cluster. 
# The Debezium PostgreSQL connector is a powerful tool that allows you to stream changes from a PostgreSQL database to a Kafka topic. It provides a simple and efficient way to capture changes in the database and process them in real-time. 
# We hope that this article has been helpful in getting you started with the Debezium PostgreSQL connector. If you have any questions or feedback, please let us know in the comments below.

# Add connector
curl http://localhost:8093/connectors -X POST -H 'Content-Type: application/json' -k -u postgres:postgres -d '{
  "name": "debezium-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname" : "real-estate",
    "database.server.name": "dbserver1",
    "plugin.name": "pgoutput",
    "topic.prefix": "real-estate-"
  }
}'
