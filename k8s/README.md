# Kubernetes

Instructions for deploying to Kubernetes.

## Artifacts

* namespaces.yaml - Manifest of namespaces that will be used.
* cert-manager.yaml - Deploys cert-manager. Used by flink.
* debezium-deployment.yaml - Deploys an instance of debezium. Reads from postgresqldb and sends messages to kafka topic.
* flink-operator.yaml - Deploys the flink operator. 
* flink-cluster.yaml - Deploys a flink cluster in session mode.
* kafka-operatory.yaml - Deploys the kafka-operator.
* kafka-single-node.yaml - Deploys a kafka cluster instance.
* kafka-ui-deployment.yaml - Deploys the kafka-ui web application.
* postgresql-statefulset.yaml - Deploys a postgresql db instance.
* pgadmin-statefulset.yaml - Deplosy pgadmin ui web application.
* add-connector.sh - Calls the debezium API and adds a connector for postgresql.

## Deployment

This document assumes you have a kubernetes cluster running. This was intially developed using minikube on a macbook. Eventually this will all be helm and you won't have to perform as many steps.

1. Deploy Namespaces
```
kubectl apply -f namespaces.yaml
```

2. Deploy cert-manager
```
kubectl apply -f cert-manager.yaml
```

3. Update the postgresql-statefulset.yaml. Set the password in the configmap. 
```
apiVersion: v1
kind: ConfigMap
metadata:
  name: postgres-secret
  namespace: postgresql
  labels:
    app: postgres
data:
  POSTGRES_DB: real_estate
  POSTGRES_USER: postgresql
  POSTGRES_PASSWORD: ##PUT YOUR PASSWORD HERE##
```
4. Deploy Postgresql
```
kubectl apply -f postgresql-statefulset.yaml
```

5. Update pgadmin-statefulset.yaml. Set the password of pgadmin.
```
        - name: PGADMIN_DEFAULT_PASSWORD
          value: ##PUT YOUR PGADMIN PASSWORD HERE ##
```

6. Deploy pgadmin-ui
```
kubectl apply -f pgadmin-statefulset.yaml
```

7. Update debesium-deployment.yaml. Set the database password to the password you defined in step 3.
```
        - name: DATABASE_PASSWORD
          value: ##PUT YOUR PASSWORD HERE###
```

8. Deploy debezium
```
kubectl apply -f debezium-deployment.yaml
```

9. Deploy kafka operator.
```
kubectl apply -f kafka-operator.yaml
```

10. Deploy kafka cluster.
```
kubectl apply -f kafka-single-node.yaml
```

11. Deploy kafka topic.
```
kubectl apply -f kafka-topic.yaml
```

12. Expose the pgadmin UI.
```
kubectl -n postgresql port-forward pod/pgadmin-0 8082:8082
```

13. Open a browser and navigate to pgadmin UI address http://127.0.0.1:8082. Connect to the cluster. The database host is postgresql.postgresql.svc.cluster.local. Run the following on the database.
```
ALTER SYSTEM SET wal_level = logical;
```

14. Restart the pod. This will cause a new pod to start up. 
```
kubectl delete pod postgresql-0 -n postgresql
```

15. Edit add-connector.sh. Set the db password to the password you defined in step 3. This should probably be a k8s job.
```
#!/bin/bash

# Add connector
curl http://127.0.0.1:8083/connectors -X POST -H 'Content-Type: application/json' -k -u postgres:postgres -d '{
  "name": "debezium-postgres-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "database.hostname": "postgresql.postgresql.svc.cluster.local",
    "database.port": "5432",
    "database.user": "postgresql",
    "database.password": "##PUT YOUR PASSWORD HERE##",
    "database.dbname" : "real_estate",
    "database.server.name": "dbserver1",
    "plugin.name": "pgoutput",
    "topic.prefix": "real-estate"
  }
}'
```

16. Expose the debezium api. NOTE: Your pod name will be different.
```
kubectl -n debezium port-forward pod/debezium-67fb8b8886-gnncd 8083:8083
```

17. Add connector to debezium.
```
./add-connector.sh
```

18. Deploy the flink operator.
```
kubectl apply -f flink-operator.yaml
```

19. Deploy the flink cluster.
```
kubectl apply -f flink-cluster.yaml
```

20. Verify the deployment.
```
kubectl get pods -a
```
You should see something similar to below.

```
NAMESPACE      NAME                                                     READY   STATUS    RESTARTS        AGE
cert-manager   cert-manager-5f68dcc8cd-tf79c                            1/1     Running   2 (5h42m ago)   33h
cert-manager   cert-manager-cainjector-59f4df9856-j99cc                 1/1     Running   6 (5h41m ago)   33h
cert-manager   cert-manager-webhook-5865dc7cfd-4sztn                    1/1     Running   4 (5h41m ago)   33h
debezium       debezium-67fb8b8886-gnncd                                1/1     Running   0               26h
flink          basic-session-deployment-only-example-56cb85556b-g5tbj   1/1     Running   0               24h
flink          flink-kubernetes-operator-d8546dfff-nkrz9                2/2     Running   2 (5h28m ago)   24h
kafka          kafka-ui-5d74985c6d-r6pw4                                1/1     Running   0               27h
kafka          my-cluster-dual-role-0                                   1/1     Running   1 (5h42m ago)   33h
kafka          my-cluster-entity-operator-d6c5c645-9x7np                2/2     Running   0               33h
kafka          strimzi-cluster-operator-b9c59999f-2tvvn                 1/1     Running   60 (23m ago)    33h
kube-system    coredns-7db6d8ff4d-pd2r2                                 1/1     Running   1 (37h ago)     38h
kube-system    etcd-minikube                                            1/1     Running   1 (37h ago)     38h
kube-system    kube-apiserver-minikube                                  1/1     Running   3 (5h41m ago)   38h
kube-system    kube-controller-manager-minikube                         1/1     Running   1 (37h ago)     38h
kube-system    kube-proxy-bj49l                                         1/1     Running   1 (37h ago)     38h
kube-system    kube-scheduler-minikube                                  1/1     Running   1 (37h ago)     38h
kube-system    storage-provisioner                                      1/1     Running   15 (60s ago)    38h
postgresql     pgadmin-0                                                1/1     Running   1 (37h ago)     38h
postgresql     postgresql-0                                             1/1     Running   0               26h
```

21. Add records to the database. Go to pgadmin and run the following in the real_estate database.
```
CREATE TABLE assessments (
    id SERIAL PRIMARY KEY,
    assessment_id UUID NOT NULL,
    assessment_date DATE NOT NULL,
    assessor_name VARCHAR(255) NOT NULL,
    status VARCHAR(255),
    notes TEXT
);

INSERT INTO assessments (assessment_id, assessment_date, assessor_name, status, notes) 
VALUES 
('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', '2022-01-01', 'John Doe', 'Good', 'No floors or windows but livable'),
('b3ba3d88-f441-45fa-a5d2-8a8b9a793b73', '2024-02-01', 'Jane Smith', 'Needs Improvement', 'The roof is missing'),
('c6c22b64-7e8a-45b9-8a7e-4a7a3b64c189', '2022-03-01', 'John Doe', 'Excellent', 'Best house I have ever seen'),
('d7d9e39a-5e1f-46a0-8f8e-77a8a596b7a2', '2021-04-01', 'Jane Smith', 'Poor', 'Not a great property');
```

22. Expose the kafka UI.
```
kubectl -n kafka port-forward pod/kafka-ui-5d74985c6d-r6pw4 8080:8080
```

23. Open a browser and navigate to pgadmin UI address http://127.0.0.1:8080. Verify there are messages in the real-estate topic.
