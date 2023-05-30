# pulceo-node-agent

## Requirements

### Middleware

An MQTT broker must be run on the system locally, for example [eclipse-mosquitto](https://mosquitto.org/). Consider using `mqtt/docker-compose.yml`.

### Dependencies

* [iperf3](https://iperf.fr/iperf-download.php) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`.
* [hping]() must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`.

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)