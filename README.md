# pulceo-node-agent

## Deploy with k3d

### Prerequisites

- Make sure that the following ports are available on the local system:
  - `80/tcp` (for testing workloads)
  - `40475/tcp` (for k3d API server)
  - `1883/tcp` (for MQTT broker)
  - `7676/tcp` (for communication with Restful API of pulceo-node-agent)
  - `4001/udp` (nping latency checks)
  - `4002/tcp` (nping latency checks)
  - `5000-5015/tcp` (iperf3 bandwidth checks)
  - `5000-5015/udp` (iperf3 bandwidth checks)
- Install Docker on your machine
- Install k3d in your machine
- Create a temporary folder on the system to store the kubeconfig file, which is required by pulceo-node-agent
```bash
mkdir /tmp/pulceo-node-agent
```
- Create a test cluster with k3d
```bash
k3d cluster create pna-test --api-port 40475 --k3s-arg "--disable=traefik@server:0" --port 80:80@loadbalancer --volume /tmp/pulceo-node-agent/:/home/pulceo
```
- Copy kubeconfig to the newly created temporary folder
```bash
cat ~/.kube/config > /tmp/pulceo-node-agent/.k3s.yaml
sed -i 's/https:\/\/0.0.0.0:40475/https:\/\/10.43.0.1:443/' /tmp/pulceo-node-agent/.k3s.yaml
```
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
