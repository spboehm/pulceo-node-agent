<img src="docs/assets/pulceo-logo-color.png" alt="pulceo-logo" width="25%" height="auto"/>

# pulceo-node-agent

[OpenAPI definition for pulceo-node-agent](https://spboehm.github.io/pulceo-node-agent/)

## General Prerequisites

- Make sure that the following ports are available on the local system:
  - `80/tcp` (for testing workloads)
  - `443/tcp` (for let's encrypt)
  - `40475/tcp` (for k3d API server)
  - `1883/tcp` (for MQTT broker)
  - `7676/tcp` (for communication with Restful API of pulceo-node-agent)
  - `4001/udp` (nping latency checks)
  - `4002/tcp` (nping latency checks)
  - `5000-5015/tcp` (iperf3 bandwidth checks)
  - `5000-5015/udp` (iperf3 bandwidth checks)
- Any Linux distribution is recommended (tested on Ubuntu 20.04 and openSUSE Tumbleweed)

## Quickstart (public deployment with Let's Encrypt)

- First, deploy pulceo on another machine [pulceo-resource-manager#quickstart](https://github.com/spboehm/pulceo-resource-manager?tab=readme-ov-file#quickstart-try-locally)
- The machine that is running the pulceo-node-agent must have a public IP address with a fully qualified domain name (FQDN)
- Ports listed under General Prerequisites must be open on the machine

Export the following environment variables
```bash
# OPTIONAL: if you want to skip the username and password generation tool 
export PNA_MQTT_BROKER_URL="ssl://broker.hivemq.com:1883"
export PNA_MQTT_CLIENT_USERNAME="<USERNAME>"
export PNA_MQTT_CLIENT_PASSWORD="<PASSWORD>"
export PNA_USERNAME="<USERNAME>"
export PNA_PASSWORD="<PASSWORD>"
export PNA_INIT_TOKEN="<INIT>"
export PNA_HOST_FQDN="<FQDN>"
```

```bash
bash <(curl -s https://raw.githubusercontent.com/spboehm/pulceo-node-agent/main/bootstrap-pulceo-node-agent.sh)
```

## Create a free MQTT broker (recommended)

- Create a basic MQTT broker on [HiveMQ](https://console.hivemq.cloud/?utm_source=HiveMQ+Pricing+Page&utm_medium=serverless+signup+CTA+Button&utm_campaign=HiveMQ+Cloud+PaaS&utm_content=serverless)
- Make sure that you select the free plan: Serverless (Free)

## Create your own MQTT broker (optional)

**TODO: Add a guide on how to create a local MQTT broker**

## Run with k3d

- Install [k3d](https://k3d.io/v5.6.0/#learning) on your machine by following the official installation guide
- Create a temporary folder on the system to store the kubeconfig file, which is required by pulceo-node-agent
```bash
mkdir -p /tmp/pulceo-node-agent
```
- Create a test cluster with k3d
```bash
k3d cluster create pna-test --api-port 40475 --k3s-arg "--disable=traefik@server:0" --port 80:80@loadbalancer --volume /tmp/pulceo-node-agent/:/home/pulceo
```
- Copy kubeconfig to the newly created temporary folder
```bash
cat ~/.kube/config > /tmp/pulceo-node-agent/.k3s.yaml
cat cat ~/.kube/config > .k3s.yaml
sed -i 's/https:\/\/0.0.0.0:40475/https:\/\/10.43.0.1:443/' /tmp/pulceo-node-agent/.k3s.yaml
```
**[TODO]: Add a step to generate the secrets**
- Apply the following kubernetes manifest to the cluster
```bash
kubectl --kubeconfig=/home/$USER/.kube/config create secret generic pna-credentials \
  --from-literal=PNA_MQTT_BROKER_URL=${PNA_MQTT_BROKER_URL} \
  --from-literal=PNA_MQTT_CLIENT_USERNAME=${PNA_MQTT_CLIENT_USERNAME} \
  --from-literal=PNA_MQTT_CLIENT_PASSWORD=${PNA_MQTT_CLIENT_PASSWORD} \
  --from-literal=PNA_USERNAME=${PNA_USERNAME} \
  --from-literal=PNA_PASSWORD=${PNA_PASSWORD} \
  --from-literal=PNA_INIT_TOKEN=${PNA_INIT_TOKEN}
kubectl apply -f traefik/0-crd.yaml
kubectl apply -f traefik/1-crd.yaml
kubectl apply -f traefik/2-traefik-services.yaml
kubectl apply -f traefik/3-deployments.yaml
kubectl apply -f traefik/4-routers-locally.yaml
```
- Check if everything is running with: `kubectl get deployment`
```
NAME                READY   UP-TO-DATE   AVAILABLE   AGE
traefik             1/1     1            1           48m
pulceo-node-agent   1/1     1            1           48m
```
- Check the exposed services with: `k3d cluster list`
```
NAME                            TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           AGE
kubernetes                      ClusterIP      10.43.0.1       <none>        443/TCP                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           60m
pulceo-node-agent               ClusterIP      10.43.61.223    <none>        7676/TCP                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          10m
traefik                         LoadBalancer   10.43.167.66    172.22.0.2    7676:30546/TCP,443:32201/TCP                                                                                                                                                                                                                                                                                                                                                                                                                                                                      10m
pulceo-node-agent-link-checks   LoadBalancer   10.43.186.122   172.22.0.2    5000:32314/UDP,5000:32314/TCP,5001:31796/UDP,5001:31796/TCP,5002:30929/UDP,5002:30929/TCP,5003:30231/UDP,5003:30231/TCP,5004:31534/UDP,5004:31534/TCP,5005:30522/UDP,5005:30522/TCP,5006:30649/UDP,5006:30649/TCP,5007:30023/UDP,5007:30023/TCP,5008:30563/UDP,5008:30563/TCP,5009:30355/UDP,5009:30355/TCP,5010:32199/UDP,5010:32199/TCP,5011:30439/UDP,5011:30439/TCP,5012:30207/UDP,5012:30207/TCP,5013:31873/UDP,5013:31873/TCP,5014:30223/UDP,5014:30223/TCP,5015:32155/UDP,5015:32155/TCP   10m
```

pulceo-node-agent is now running and ready to accept workloads under `http://EXTERNAL-IP:7676`. 

```bash
curl -I http://172.22.0.2:7676/health
```
```
HTTP/1.1 200 OK
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Content-Length: 0
Date: Mon, 26 Feb 2024 23:32:12 GMT
Expires: 0
Pragma: no-cache
X-Content-Type-Options: nosniff
X-Frame-Options: DENY
X-Xss-Protection: 0
```
