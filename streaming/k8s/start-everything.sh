#!/bin/bash

kubectl apply -f namespaces.yaml
sleep 10
kubectl apply -f cert-manager.yaml
sleep 10

# Uncomment if you want to play with CDC
# kubectl apply -f postgresql-statefulset.yaml
# sleep 10
# kubectl apply -f pgadmin-statefulset.yaml
# sleep 10
# kubectl apply -f debezium-deployment.yaml
# sleep 10

kubectl apply -f kafka-operator.yaml
sleep 10
kubectl apply -f kafka-single-node.yaml
sleep 10
kubectl apply -f kafka-topic.yaml
sleep 10
kubectl apply -f kafka-ui-deployment.yaml
sleep 10
kubectl apply -f flink-operator.yaml
sleep 10
kubectl apply -f flink-cluster.yaml
sleep 10
kubectl apply -f minio-operator.yaml
sleep 10
kubectl apply -f minio-tenant.yaml
sleep 10
