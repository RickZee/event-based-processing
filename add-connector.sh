#!/bin/bash

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
    "topic.prefix": "real-estate"
  }
}'
