# Requirements

## Functional requirements



### Links

A link describes a physical or logical connection between homogeneous resources. 
Possible resources are `Nodes`, `Groups`, or `Workloads`.

#### Link-related operations

| ID   | Requirement                                                                                        | Status |
|------|----------------------------------------------------------------------------------------------------|--------|
| lro1 | The system must be able to create a link between resources of type nodes, groups, or workloads.    | OPEN   |
| lro2 | The system must be able to read a link between resources of type nodes, groups, or workloads.      | OPEN   |
| lro3 | The system must be able to update a link between resources of type nodes, groups, or workloads.    | OPEN   |
| lro4 | The system must be able to delete a link between resources of type nodes, groups, or workloads.    | OPEN   |
| lro5 | The system must be able to prevent that a link is created or updated with heterogeneous resources. | OPEN   |

#### Link-related metrics

| ID   | Metric | Requirement                                                                                          | System library | Service class    | Status   |
|------|--------|------------------------------------------------------------------------------------------------------|----------------|------------------|----------|
| lrm1 | RTD    | The system must be able to measure the round-trip delay using ICMP to another node of any type.      | ping           |                  | Open     |
| lrm2 | RTD    | The system must be able to measure the round-trip delay using TCP to another node of any type.       | nping          |                  | Open     |
| lrm3 | RTD    | The system must be able to measure the round-trip delay using UDP to another node of any type.       | nping          | DelayService     | PROGRESS |
| lrm4 | E2E    | The system must be able to measure the end-to-end delay using ICMP to another node of any type.      | ping           |                  | Open     |
| lrm5 | E2E    | The system must be able to measure the end-to-end delay using TCP to another node of any type.       | nping          |                  | Open     |
| lrm6 | E2E    | The system must be able to measure the end-to-end delay using UDP to another node of any type.       | nping          |                  | Open     |
| lrm7 | TCP-BW | The system must be able to measure the bandwidth / throughput using TCP to another node of any type. | iperf          | BandwidthService | CLOSED   |
| lrm8 | UDP-BW | The system must be able to measure the bandwidth / throughput using UDP to another node of any type. | iperf          | BandwidthService | CLOSED   |
