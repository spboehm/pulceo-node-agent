# pulceo-node-agent

## Requirements

### Middleware

An MQTT broker must be run on the system locally, for example [eclipse-mosquitto](https://mosquitto.org/). Consider using `mqtt/docker-compose.yml`.

### Dependencies

* [iperf3](https://iperf.fr/iperf-download.php) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`. Only version 3 is supported.
* [hping](https://github.com/antirez/hping) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`. For openSUSE Tumbleweed, the package `libcap-progs` must be installed. Additionally, `sudo setcap cap_net_raw+ep /usr/bin/nping` must be executed.

### Low-level libraries

#### Nping

* General structure: `nping [Probe mode] [Options] {target specification}`
* Round-trip delay (RTD) with protocol ICMP: `nping --icmp `

#### Iperf

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)