#!/bin/bash
kubectl config use-context k3d-pna-test
mkdir -p .k3s-service-account
kubectl get secret k3s-serving -n kube-system -o jsonpath="{['data']['tls\.crt']}" | base64 --decode | tail -n 10 > .k3s-service-account/ca.crt
kubectl create serviceaccount cluster-admin
kubectl create clusterrolebinding cluster-admin-manual --clusterrole=cluster-admin --serviceaccount=default:cluster-admin
# kubectl get secret $(kubectl get serviceaccount cluster-admin -o jsonpath="{['secrets'][0]['name']}") -o jsonpath="{['data']['ca\.crt']}" | base64 --decode > .k3s-service-account/ca.crt
kubectl create token cluster-admin > .k3s-service-account/token