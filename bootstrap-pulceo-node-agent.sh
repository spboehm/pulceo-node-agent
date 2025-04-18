#!/bin/bash
set -e
set -o noglob

generate_password() {
  local chars=$1
  head /dev/urandom | tr -dc A-Za-z0-9 | head -c $chars ; echo ''
}

validate_mqtt_broker_url() {
  local url=$1
  local regex="^ssl://[a-zA-Z0-9.-]+:[0-9]+$"

  if [[ $url =~ $regex ]]; then
    echo "Valid MQTT broker URL"
  else
    echo "Invalid MQTT broker URL"
    exit 1
  fi
}

validate_alphanumeric() {
  local key=$1
  local str=$2
  local regex="^[a-zA-Z0-9]{8,}$"

  if [[ $str =~ $regex ]]; then
    echo "Valid" "$key"
  else
    echo "Invalid" "$key"
    exit 1
  fi
}

function check_version() {
  local version=$1
  local service=$2

  if [ -z "$version" ]; then
    read -p "Enter the version for $service (should be like v1.0.0): " version
    if [[ ! $version =~ ^v[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
      version="v1.0.0"
    fi
  fi

  echo $version
}

echo ""
echo "PULCEO NODE AGENT - Bootstrapping tool. USE AT OWN RISK!!!"
echo ""

echo "Check if a .env-pna file is present..."
if [ -f .env-pna ]; then
  echo "Found .env-pna file...loading variables..."
  source .env-pna
else
  echo "No .env-pna file found...creating one..."
fi

# PNA_VERSION
PNA_VERSION=$(check_version "$PNA_VERSION" "PNA")
echo "USE PNA_VERSION=$PNA_VERSION"

# PNA_MQTT_BROKER_URL
if [ -z "$PNA_MQTT_BROKER_URL" ]; then
  read -p "Enter the MQTT broker URL (should be like ssl://be3187d06377498a1eee29fd8f09495d.s1.eu.hivemq.cloud:8883): " PNA_MQTT_BROKER_URL
fi
validate_mqtt_broker_url $PNA_MQTT_BROKER_URL

# PNA_MQTT_CLIENT_USERNAME
if [ -z "$PNA_MQTT_CLIENT_USERNAME" ]; then
  EXAMPLE_MQTT_CLIENT_USERNAME=$(generate_password 8)
  read -p "Enter the MQTT client username (should be like $EXAMPLE_MQTT_CLIENT_USERNAME): ENTER TO ACCEPT" PNA_MQTT_CLIENT_USERNAME
  if [ -z "$PNA_MQTT_CLIENT_USERNAME" ]; then
    PNA_MQTT_CLIENT_USERNAME=$EXAMPLE_MQTT_CLIENT_USERNAME
  fi
fi
validate_alphanumeric "PNA_MQTT_CLIENT_USERNAME" $PNA_MQTT_CLIENT_USERNAME

# PNA_MQTT_CLIENT_PASSWORD
if [ -z "$PNA_MQTT_CLIENT_PASSWORD" ]; then
  EXAMPLE_MQTT_CLIENT_PNA_MQTT_CLIENT_PASSWORD=$(generate_password 8)
  read -p "Enter the MQTT client password (should be like $EXAMPLE_MQTT_CLIENT_PNA_MQTT_CLIENT_PASSWORD): ENTER TO ACCEPT" PNA_MQTT_CLIENT_PASSWORD
  if [ -z "$PNA_MQTT_CLIENT_PASSWORD" ]; then
    PNA_MQTT_CLIENT_PASSWORD=$EXAMPLE_MQTT_CLIENT_PNA_MQTT_CLIENT_PASSWORD
  fi
fi
validate_alphanumeric "PNA_MQTT_CLIENT_PASSWORD" $PNA_MQTT_CLIENT_PASSWORD

# PNA_USERNAME
# 24 chars
if [ -z "$PNA_USERNAME" ]; then
  read -p "Enter the PNA username: " PNA_USERNAME
  if [ -z "$PNA_USERNAME" ]; then
    exit 1
  fi
fi
validate_alphanumeric "PNA_USERNAME" $PNA_USERNAME

# PNA_PASSWORD
# 32 chars
if [ -z "$PNA_PASSWORD" ]; then
  read -p "Enter the PNA password: " PNA_PASSWORD
  if [ -z "$PNA_PASSWORD" ]; then
    exit 1
  fi
fi
validate_alphanumeric "PNA_PASSWORD" $PNA_PASSWORD


if [ -z "$PNA_HOST_FQDN" ]; then
  read -p "Enter the PNA host fqdn: " PNA_HOST_FQDN
  if [ -z "$PNA_HOST_FQDN" ]; then
    exit 1
  fi
fi

# PNA_INIT_TOKEN
PNA_INIT_TOKEN=$(echo -n "${PNA_USERNAME}:${PNA_PASSWORD}" | base64)

echo "PNA_VERSION=$PNA_VERSION" > .env-pna
echo "PNA_MQTT_BROKER_URL=$PNA_MQTT_BROKER_URL" >> .env-pna
echo "PNA_MQTT_CLIENT_USERNAME=$PNA_MQTT_CLIENT_USERNAME" >> .env-pna
echo "PNA_MQTT_CLIENT_PASSWORD=$PNA_MQTT_CLIENT_PASSWORD" >> .env-pna
echo "PNA_USERNAME=$PNA_USERNAME" >> .env-pna
echo "PNA_PASSWORD=$PNA_PASSWORD" >> .env-pna
echo "PNA_INIT_TOKEN=$PNA_INIT_TOKEN" >> .env-pna
echo "PNA_HOST_FQDN=$PNA_HOST_FQDN" >> .env-pna

echo "Successfully created .env-pna file with all credentials...DO NOT SHARE THIS FILE WITH ANYONE!!!"

DOMAIN=$PNA_HOST_FQDN
if [ -z "$OVERRIDE_DNS" ]; then
  echo "Install K3s with NOT overridden DNS..."
  curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable=traefik --node-name=pna-k8s-node" sh -
else
  echo "Install K3s with overridden DNS..."
  curl -sfL https://get.k3s.io | INSTALL_K3S_EXEC="--disable=traefik --node-name=pna-k8s-node --resolv-conf /home/$USER/resolv.conf" sh -
fi

mkdir -p /home/$USER/.kube
sudo cat /etc/rancher/k3s/k3s.yaml > /home/$USER/.kube/config
chown -R $USER:$USER /home/$USER/.kube/config
chmod -R 755 /home/$USER/.kube/config
sudo cat /etc/rancher/k3s/k3s.yaml > /home/$USER/.k3s.yaml
chown -R $USER:$USER /home/$USER/.k3s.yaml
chmod -R 755 /home/$USER/.k3s.yaml
sed -i 's/https:\/\/127.0.0.1:6443/https:\/\/10.43.0.1:443/' /home/$USER/.k3s.yaml
kubectl --kubeconfig=/home/$USER/.kube/config create namespace pulceo
kubectl --kubeconfig=/home/$USER/.kube/config create secret generic pna-credentials \
  --from-literal=PNA_MQTT_BROKER_URL=${PNA_MQTT_BROKER_URL} \
  --from-literal=PNA_MQTT_CLIENT_USERNAME=${PNA_MQTT_CLIENT_USERNAME} \
  --from-literal=PNA_MQTT_CLIENT_PASSWORD=${PNA_MQTT_CLIENT_PASSWORD} \
  --from-literal=PNA_USERNAME=${PNA_USERNAME} \
  --from-literal=PNA_PASSWORD=${PNA_PASSWORD} \
  --from-literal=PNA_INIT_TOKEN=${PNA_INIT_TOKEN} \
  --from-literal=PNA_HOST_FQDN=${PNA_HOST_FQDN} \
  --from-literal=PNA_UUID=$(uuidgen)
kubectl --kubeconfig=/home/$USER/.kube/config apply -f https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/0-crd.yaml
kubectl --kubeconfig=/home/$USER/.kube/config apply -f https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/1-crd.yaml
kubectl --kubeconfig=/home/$USER/.kube/config apply -f https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/2-traefik-services.yaml
kubectl --kubeconfig=/home/$USER/.kube/config apply -f https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/3-deployments.yaml
curl -s https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/3-deployments.yaml | sed 's/\/home\/pulceo/\/home\/'"$USER"'/g' | kubectl --kubeconfig=/home/$USER/.kube/config apply -f -
curl -s https://raw.githubusercontent.com/spboehm/pulceo-node-agent/$PNA_VERSION/traefik/4-routers.yaml | sed 's/localhost.localdomain/'"$DOMAIN"'/g' | kubectl --kubeconfig=/home/$USER/.kube/config apply -f -
