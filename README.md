# pulceo-node-agent

## Requirements

### Dependencies

* [iperf3](https://iperf.fr/iperf-download.php) must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`.
* [hping]() must be installed on the system. The pulceo-node-agent does directly use the binary located in `bin`.

### Firewall

* TCP/UDP, port 5000 - 5015 (depending on `pna.iperf3.max.server.instances` in `application.properties`)