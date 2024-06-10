#!/bin/bash

kubectl apply -f namespaces.yaml
kubectl apply -f cert-manager.yaml
kubectl apply -f postgresql-statefulset.yaml
kubectl apply -f pgadmin-statefulset.yaml
kubectl apply -f debezium-deployment.yaml
kubectl apply -f kafka-operator.yaml
kubectl apply -f kafka-single-node.yaml
kubectl apply -f kafka-topic.yaml
kubectl apply -f flink-operator.yaml
kubectl apply -f flink-cluster.yaml
kubectl apply -f minio-operator.yaml
kubectl apply -f minio-tenant.yaml
