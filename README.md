# pulceo-node-agent

## Requirements

### Middleware

An MQTT broker must be run on the system locally, for example [eclipse-mosquitto](https://mosquitto.org/). Consider using `mqtt/docker-compose.yml`.

### Dependencies

* [iperf3](https://iperf.fr/iperf-download.php) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`. Only version 3 is supported.
* [nping](https://nmap.org/nping/) must be installed on the system. The pulceo-node-agent does directly use the binary located in `usr`. For openSUSE Tumbleweed, the package `libcap-progs` must be installed. Additionally, `sudo setcap cap_net_raw+ep /usr/bin/nping` must be executed.

### Low-level libraries

#### ping

* General structure : `ping  [OPTIONS] {destination}`
* Round-trip delay with protocol ICMP: `ping -4 s 66 -I eth0`

#### Nping

* General structure: `nping [Probe mode] [Options] {target specification}`
* Round-trip delay (RTD) with protocol TCP: `/usr/bin/nping --tcp-connect -p 8080 -c 20 localhost"`
* Round-trip delay (RTD) with protocol UDP: `/usr/bin/nping --udp -p 8080 -c 20 localhost`

#### Iperf

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)