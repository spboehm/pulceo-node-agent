# pulceo-node-agent

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
* Throughput with TCP: `/bin/iperf3 -s -p 5001 -f m --bind-dev lo` (server), `/bin/iperf3 -c localhost -p 5001 -n 10 -f m --bind-dev lo` (client)
* Throughput with UDP: `/bin/iperf3 -s -p 5001 -f m --bind-dev lo` (server), `/bin/iperf3 -c localhost -u -p 5001 -b 1M -n 10 -f m --bind-dev lo` (client) 


### Endpoints

* UDP/4001: Endpoint for measuring latency via UDP
* TCP/4002: Endpoint for measuring latency via TCP

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)

### Messages

#### Cloud



#### PingResult

```json
{
  "deviceId": "pna-0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "8fd3c6b8-4d4c-438f-8e28-e642d866a5c0",
    "jobUUID": "d6093440-bf51-4e36-87c0-5c8026f4757f",
    "metricType": "PING_ICMP",
    "metricResult": {
      "sourceHost": "localhost",
      "destinationHost": "localhost",
      "startTime": "2023-11-11T01:30:10.087823670Z",
      "endTime": "2023-11-11T01:30:10.092789044Z",
      "pingDelayMeasurement": {
        "uuid": "615091cc-6bef-4949-931d-9ec61bd9a22f",
        "packetsTransmitted": 1,
        "packetsReceived": 1,
        "packetLoss": 0.0,
        "time": 0,
        "rttMin": 0.042,
        "rttAvg": 0.042,
        "rttMax": 0.042,
        "rttMdev": 0.0
      }
    }
  }
}
```

### NpingTCPResult

```json
{
  "deviceId": "pna-0247fea1-3ca3-401b-8fa2-b6f83a469680",
  "metric": {
    "metricUUID": "0ad1545b-9058-4fd4-85ac-5dfddb6bc087",
    "jobUUID": "56159fab-8aae-4e85-908d-58b337f108e2",
    "metricType": "NPING_TCP",
    "metricResult": {
      "sourceHost": "localhost",
      "destinationHost": "localhost",
      "startTime": "2023-11-11T01:34:35.490202817Z",
      "endTime": "2023-11-11T01:34:35.496742587Z",
      "npingTCPDelayMeasurement": {
        "uuid": "8bce9dba-1a6f-4f5a-8274-406ae877658b",
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
