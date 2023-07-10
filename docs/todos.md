# TODOs

## UdpConfig

- [] Replace UdpEchoServer with traditional Java-based configuration

## BandwidthService 

- [] Implement dedicated port ranges for TCP and UDP bandwidth measurements, update `pna.iperf3.max.server.instances` accordingly.
- [] Implement test with only one measurement result, otherwise tests last pretty long
- [] Check client and sender semantics, respectively naming
- [] Ensure that particular interfaces can be used for measuring bandwidth

## DelayService

- [] Ensure that multiple backends are available for determining the latency, example (`nping` and `ping`)

## JobService

- [] Ensure that status flag of job is set properly in `scheduleIperfJob(...)`
- [] Ensure that all jobs are rescheduled after application crash
- [] Ensure that cancellation of jobs properly sets the active flag

## NpingUtils

- [] Run tests with different output parameters, in case output is falsely parsed
- [] Ensure that particular interfaces can be used for determining the delay, e.g., via env vars

## Iperf3utils

- [] Check for performance impacts of parameter `-Z` for iperf3 measurements.
- [] Validate parameters for creating server and client processes
- [] Run tests withs different output parameters, in case output is falsely parsed
- [] Check if output is correctly processed even if iperf has failed to parse the output
- [] Check client and sender semantics, respectively naming
- [] Ensure that particular interfaces can be determined for measuring the bandwidth, e.g., via env vars
