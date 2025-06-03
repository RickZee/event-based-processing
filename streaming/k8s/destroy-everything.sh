#!/bin/bash

kubectl delete -f cert-manager.yaml
kubectl delete -f debezium-deployment.yaml
kubectl delete -f flink-operator.yaml
kubectl delete -f flink-cluster.yaml
kubectl delete -f kafka-operatory.yaml
kubectl delete -f kafka-single-node.yaml
kubectl delete -f kafka-ui-deployment.yaml
kubectl delete -f postgresql-statefulset.yaml
kubectl delete -f pgadmin-statefulset.yaml
kubectl delete -f minio-operator.yaml
kubectl delete -f minio-tenant.yaml
kubectl delete -f namespaces.yaml
