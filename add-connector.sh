#!/bin/bash

# Add connector
curl https://localhost:8083/connectors -X POST -H 'Content-Type: application/json' -k -u kc_username:kc_password -d '{
  "name": "debezium-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgres",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname" : "postgres",
    "database.server.name": "dbserver1",
    "plugin.name": "pgoutput"
  }
}'
