#!/usr/bin/env bash
mvn -Plinux package -DskipTests=true -Dmaven.javadoc.skip=true
docker build -t dejankovacevic/cryptonite:latest .
docker push dejankovacevic/cryptonite
kubectl delete pod -l name=cryptonite -n prod
kubectl get pods -l name=cryptonite -n prod

