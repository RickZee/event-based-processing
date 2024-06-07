#!/bin/bash

# Get a list of all namespaces
namespaces=$(kubectl get namespaces -o jsonpath='{.items[*].metadata.name}')

for namespace in $namespaces; do
  if [ $namespace == "kube-system" ] || [ $namespace == "kube-public" ] || [ $namespace == "default" ]; then
    continue
  fi

  echo "Deleting services in namespace $namespace"
  kubectl delete services --all -n $namespace

  echo "Deleting pods in namespace $namespace"
  kubectl delete pods --all -n $namespace

  echo "Deleting daemonsets in namespace $namespace"
  kubectl delete daemonsets --all -n $namespace

  echo "Deleting deployments in namespace $namespace"
  kubectl delete deployments --all -n $namespace

  echo "Deleting statefulsets in namespace $namespace"
  kubectl delete statefulsets --all -n $namespace

  echo "Deleting jobs in namespace $namespace"
  kubectl delete jobs --all -n $namespace

  echo "Deleting replicasets in namespace $namespace"
  kubectl delete replicasets --all -n $namespace
done
