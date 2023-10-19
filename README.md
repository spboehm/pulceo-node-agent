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