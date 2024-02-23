#!/bin/bash
# TODO: export statements
curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable=traefik" sh -
mkdir -p /home/$USER/.kube
sudo cat /etc/rancher/k3s/k3s.yaml > /home/$USER/.kube/config
chmod 0600 /home/$USER/.kube/config
sudo cat /etc/rancher/k3s/k3s.yaml > /home/$USER/.k3s.yaml
# TODO: replace server with in-cluster data
sed "s/\$REPLICAS/$REPLICAS/" deployment.yaml
chmod 0600 /home/$USER/.k3s.yaml
export KUBECONFIG=~/.kube/config
# wait until k3s is completed
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v2.10/docs/content/reference/dynamic-configuration/kubernetes-crd-definition-v1.yml
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v2.10/docs/content/reference/dynamic-configuration/kubernetes-crd-rbac.yml