#!/bin/bash

# Add connector (replace the IP with your `minikube ip`)
curl http://192.168.49.2/connectors -X POST -H 'Content-Type: application/json' -k -u postgres:postgres -d '{
  "name": "debezium-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgresql.postgresql.svc.cluster.local",
    "database.port": "5432",
    "database.user": "postgresql",
    "database.password": "postgres",
    "database.dbname" : "real_estate",
    "database.server.name": "dbserver1",
    "plugin.name": "pgoutput",
    "topic.prefix": "real-estate"
  }
}'
