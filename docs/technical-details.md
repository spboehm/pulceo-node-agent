# pulceo-node-agent

## Develop

* Install k3d on your system: `k3d cluster create pna-test --api-port 40475 --k3s-arg "--disable=traefik@server:0" --port 80:80@loadbalancer`
* Make k3s.yaml available for pulceo-node-agent: `cp ~/.kube/config .k3s.yaml`
* Run `chmod +x bootstrap-k3s-access.sh && ./bootstrap-k3s-access.sh` to install the necessary tools and create the namespace `pulceo-node-agent

## Requirements

### Middleware

An MQTT broker must be run on the system locally, for example [eclipse-mosquitto](https://mosquitto.org/). Consider using `mqtt/docker-compose.yml`.

Just start the MQTT broker by running `docker-compose -f mqtt/docker-compose.yml up -d`.

### Dependencies

* [iperf3](https://iperf.fr/iperf-download.php) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`. Only version 3 is supported.
* [nping](https://nmap.org/nping/) must be installed on the system. The pulceo-node-agent does directly use the binary located in `usr`. For openSUSE Tumbleweed, the package `libcap-progs` must be installed. Additionally, `sudo setcap cap_net_raw+ep /usr/bin/nping` must be executed.

### Low-level libraries

#### ping

Version:

```bash
ping from iputils 20221126
libcap: yes, IDN: yes, NLS: yes, error.h: yes, getrandom(): yes, __fpending(): yes
```

* General structure : `ping [OPTIONS] {destination}`
* Round-trip delay with protocol ICMP: `/usr/bin/ping -4 -c 10 -s 66 -I eth0`

#### Nping

* General structure: `nping [Probe mode] [Options] {target specification}`
* Round-trip delay (RTD) with protocol TCP: `/usr/bin/nping -4 --tcp-connect -c 20 --dest-ip localhost -p 8080 -e eth0`. Option `--tcp-connect` determines the latency using the time difference between SYN-ACK. Therefore, no `--data-length` can be provided.
* Round-trip delay (RTD) with protocol UDP: `/usr/bin/nping -4 --udp -c 20 --dest-ip localhost -p 8080 -e eth0 --data-length 4`. The default size for `--data-length` of `nping` is 4 bytes.

#### Iperf3

* General structure: `/bin/iperf3 [-s|-c host] [options]`
* Throughput with TCP: `/bin/iperf3 -s -p 5001 -f m --bind localhost` (server), `/bin/iperf3 -c localhost -p 5001 -n 10 -f m --bind localhost` (client)
* Throughput with UDP: `/bin/iperf3 -s -p 5001 -f m --bind localhost` (server), `/bin/iperf3 -c localhost -u -p 5001 -b 1M -n 10 -f m --bind localhost` (client)


### Endpoints

* UDP/4001: Endpoint for measuring latency via UDP
* TCP/4002: Endpoint for measuring latency via TCP

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)

### Messages

#### Cloud


#### CPU UTILIZATION

```json
{
    "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
    "metric": {
        "metricUUID": "629620b5-e53e-4bed-83b7-77f4117c6690",
        "jobUUID": "a61205f1-50c3-47e8-86dc-1e546745467d",
        "metricType": "CPU_UTIL",
        "metricResult": {
            "srcHost": "127.0.0.1",
            "k8sResourceType": "NODE",
            "resourceName": "k3d-pna-test-server-0",
            "time": "2024-02-17T16:30:32Z",
            "cpuUtilizationMeasurement": {
                "uuid": "bdbaab92-b451-4ea8-a633-2b9b89ab4cc3",
                "time": "2024-02-17T16:30:32Z",
                "usageNanoCores": 37271633,
                "usageCoreNanoSeconds": 413578840000,
                "usageCPUPercentage": 4.66
            }
        }
    }
}
```

#### Memory Utilization

```json
{
    "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
    "metric": {
        "metricUUID": "9d628f02-0888-4cdd-94de-f57d0a89bbb6",
        "jobUUID": "c3cf0f41-bef8-4229-81bf-5f2d0669223f",
        "metricType": "MEM_UTIL",
        "metricResult": {
            "srcHost": "127.0.0.1",
            "k8sResourceType": "NODE",
            "resourceName": "k3d-pna-test-server-0",
            "time": "2024-02-17T17:32:46Z",
            "memoryUtilizationMeasurement": {
                "uuid": "79c68a59-a535-4025-acfd-1a4da95576b1",
                "time": "2024-02-17T17:32:46Z",
                "usageBytes": 1463914496,
                "availableBytes": 66301313024,
                "usageMemoryPercentage": 2.21
            }
        }
    }
}
```

### Storage Utilization

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "ff161744-37df-4927-9f47-50db763b02be",
    "jobUUID": "e8ae8d97-0859-4e86-9163-928870c716dc",
    "metricType": "STORAGE_UTIL",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "k8sResourceType": "NODE",
      "resourceName": "k3d-pna-test-server-0",
      "time": "2024-02-18T11:56:20Z",
      "storageUtilizationMeasurement": {
        "uuid": "28440e29-642b-4b8d-a41b-b5277fea3ca1",
        "time": "2024-02-18T11:56:20Z",
        "name": "k3d-pna-test-server-0-fs",
        "usedBytes": 363328516096,
        "capacityBytes": 497419288576,
        "usageStoragePercentage": 73.04
      }
    }
  }
}
```

#### ICMP RTT

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "99976261-b25d-4466-b522-8682496dccb2",
    "jobUUID": "f806ca34-2cea-41f4-a17e-ae3c0efcc971",
    "metricType": "ICMP_RTT",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "destinationHost": "127.0.0.1",
      "startTime": "2024-01-29T09:06:59.157516308Z",
      "endTime": "2024-01-29T09:06:59.160564103Z",
      "pingDelayMeasurement": {
        "uuid": "6d6237bf-8400-4761-ad66-502711143b23",
        "packetsTransmitted": 1,
        "packetsReceived": 1,
        "packetLoss": 0.0,
        "time": 0,
        "rttMin": 0.045,
        "rttAvg": 0.045,
        "rttMax": 0.045,
        "rttMdev": 0.0
      }
    }
  }
}
```

### TCP RTT

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "cbd29b61-5955-4743-9c62-6710bf7bcab5",
    "jobUUID": "ac122df4-32b0-4c2d-9048-6cfd7971b477",
    "metricType": "TCP_RTT",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "destinationHost": "127.0.0.1",
      "startTime": "2024-01-29T09:07:11.716082302Z",
      "endTime": "2024-01-29T09:07:11.725847321Z",
      "npingTCPDelayMeasurement": {
        "uuid": "0874f97b-58a3-48d8-b1ed-7db09884e803",
        "maxRTT": 0.007,
        "minRTT": 0.007,
        "avgRTT": 0.007,
        "tcpConnectionAttempts": 1,
        "tcpSuccessfulConnections": 1,
        "tcpFailedConnectionsAbsolute": 0,
        "tcpFailedConnectionsRelative": 0.0
      }
    }
  }
}
```

### UDP RTT

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "6cc6c101-b632-4a0c-b4d4-a5ad2a208c31",
    "jobUUID": "da409089-5605-43ff-8eba-6e7ce7038d52",
    "metricType": "UDP_RTT",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "destinationHost": "127.0.0.1",
      "startTime": "2024-01-29T09:06:56.679421866Z",
      "endTime": "2024-01-29T09:06:56.686175106Z",
      "dataLength": 4,
      "npingUDPDelayMeasurement": {
        "uuid": "3883095f-1366-4b3a-b5ff-cfa6ec8a110d",
        "maxRTT": 0.897,
        "minRTT": 0.897,
        "avgRTT": 0.897,
        "udpPacketsSent": 1,
        "udpReceivedPackets": 1,
        "udpLostPacketsAbsolute": 0,
        "udpLostPacketsRelative": 0.0
      }
    }
  }
}
```

### Iperf3 TCP BW

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "fee50cfc-1360-4713-a70e-a0827d2d6976",
    "jobUUID": "fee50cfc-1360-4713-a70e-a0827d2d6976",
    "metricType": "TCP_BW",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "destinationHost": "127.0.0.1",
      "startTime": "2024-01-29T10:32:57.465165Z",
      "endTime": "2024-01-29T10:32:58.479535020Z",
      "iperfBandwidthMeasurementReceiver": {
        "iperf3Protocol": "TCP",
        "bitrate": 15735.0,
        "bandwidthUnit": "Mbits/s",
        "iperfRole": "RECEIVER"
      },
      "iperfBandwidthMeasurementSender": {
        "iperf3Protocol": "TCP",
        "bitrate": 15738.0,
        "bandwidthUnit": "Mbits/s",
        "iperfRole": "SENDER"
      }
    }
  }
}
```

### Iperf3 UDP BW

```json
{
  "deviceId": "0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "a4524c72-f53d-449e-9b3b-37fe7904eae4",
    "jobUUID": "a4524c72-f53d-449e-9b3b-37fe7904eae4",
    "metricType": "UDP_BW",
    "metricResult": {
      "sourceHost": "127.0.0.1",
      "destinationHost": "127.0.0.1",
      "startTime": "2024-01-29T09:06:57.008627239Z",
      "endTime": "2024-01-29T09:06:58.011300946Z",
      "iperfBandwidthMeasurementReceiver": {
        "iperf3Protocol": "UDP",
        "bitrate": 32.1,
        "bandwidthUnit": "Mbits/s",
        "iperfRole": "RECEIVER",
        "jitter": 0.017,
        "lostDatagrams": 0,
        "totalDatagrams": 241
      },
      "iperfBandwidthMeasurementSender": {
        "iperf3Protocol": "UDP",
        "bitrate": 32.1,
        "bandwidthUnit": "Mbits/s",
        "iperfRole": "SENDER",
        "jitter": 0.0,
        "lostDatagrams": 0,
        "totalDatagrams": 241
      }
    }
  }
}
```

