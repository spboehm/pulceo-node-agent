# TODOs

## Overall Issues

- [ ] Inconsistent naming of bandwidth vs. iperf, e.g. in (`JobService`)
- [ ] Inconsistent naming of delay vs. npingTCP, e.g. in (`JobService`)

## Package config

- [ ] Replace UdpEchoServer with traditional Java-based configuration
- [ ] Revise all configurations, esp. `TcpConfig`, `UdpConfig`, `MQTTConfig`, and `DelayServiceConfig`

## BandwidthService 

- [ ] Implement dedicated port ranges for TCP and UDP bandwidth measurements, update `pna.iperf3.max.server.instances` accordingly
- [ ] Reference defined port ranges in `BandwidthService`
- [x] Implement test with only one measurement result, otherwise tests last pretty long
- [ ] Check client and sender semantics, respectively naming
- [ ] Ensure that particular interfaces can be used for measuring bandwidth
- [x] Improve process generation with ProcessBuilder
- [ ] Set minimum values for recurrence
- [x] Remove Job semantics for `BandwidthJob`
- [x] Add `IperfRequest` instead of `IperfJob`
- [x] Implement `testMeasureBandwidth()` in `BandwidthServiceTests`
- [ ] Also include the retr tansmission in iperf3
- [x] Include custom bitrate in iperf3
- [ ] Add object-oriented style for running 
- [ ] Add test cases for TCP

## DelayService

- [ ] Ensure that multiple backends are available for determining the latency, example (`nping` and `ping`)
- [ ] Ensure that different rounds can be defined for determining the latency
- [x] Ensure that the payload size can be specified for the latency test, at least for TCP
- [ ] Refactor `measureDelay()`, improve handling
- [ ] Improve process generation with ProcessBuilder
- [ ] Set minimum values for recurrence
- [ ] Remove Job semantics for `NpingJob`
- [ ] Add `NpingRequest` instead of `NpingJob`
- [ ] Ensure that no duplicate measurements can be created
- [x] Remove port from all methods
- [ ] Add test cases for UDP

## JobService

- [ ] Ensure that status flag of job is set properly in `scheduleIperfJob(...)`
- [ ] Ensure that all jobs are rescheduled after application crash
- [ ] Ensure that cancellation of jobs properly sets the active flag
- [ ] Enable that the result is sent via `this.delayServiceMessageChannel.send(new GenericMessage<>(npingTCPResult))`;
- [ ] Ensure that no duplicate jobs can be created
- [ ] Add appropriate logging for 
- [ ] Consider moving PublishSubscribeChannels for bandwidth and delay to another place

## NpingUtils

- [ ] Run tests with different output parameters, in case output is falsely parsed
- [ ] Ensure that particular interfaces can be used for determining the delay, e.g., via env vars
- [ ] Clarify method signature of `extractNpingTCPDelayMeasurement(...)`... First parameter is currently obsolete
- [ ] Improve command generation out of `NpingCmd` class, avoid error-prone hard-coding
- [ ] Add dataLength validation in NpingCmd and respective tests

## Iperf3utils

- [ ] Check for performance impacts of parameter `-Z` for iperf3 measurements.
- [ ] Validate parameters for creating server and client processes
- [ ] Run tests withs different output parameters, in case output is falsely parsed
- [ ] Check if output is correctly processed even if iperf has failed to parse the output
- [ ] Check client and sender semantics, respectively rename sender and receiver because it is ambiguous
- [ ] Ensure that particular interfaces can be determined for measuring the bandwidth, e.g., via env vars
- [x] Improve command generation out of `IperfCmd` class
- [x] Add additional test cases for UDP

## Application Startup

- [ ] Connection probe MQTT
- [ ] Existence of nping and iperf3 executable

## Tests

- [ ] `dev.pulceo.pna.IperfJobServiceTests` misses additional tests for job cancellation
- [ ] `dev.pulceo.pna.NpingJobServiceTests` misses additional tests for job cancellation
- [ ] `BandwidthServiceTests#startIperf3TCPSenderInstance` uses a too long sending interval. Reduce it.