#!/bin/bash
kubectl apply -f ./infrastructure/kubernetes/.
kubectl apply -f ./infrastructure/kubernetes/enable-rabbitmq/.
kubectl apply -f ./infrastructure/kubernetes/enable-database-execution/.
kubectl apply -f ./infrastructure/kubernetes/enable-database-engine/.