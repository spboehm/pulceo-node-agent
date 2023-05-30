# TODOs


## UdpConfig

- [] Replace UdpEchoServer with traditional Java-based configuration

## BandwidthService 

- [] Implement dedicated port ranges for TCP and UDP bandwidth measurements, update `pna.iperf3.max.server.instances` accordingly.
- [] Implement test with only one measurement result, otherwise tests last pretty long

## JobService

- [] Ensure that status flag of job is set properly in `scheduleIperfJob(...)`
- [] Ensure that all jobs are rescheduled after application crash
- [] Ensure that cancellation of jobs properly sets the active flag

## Iperf3utils

- [] Check for performance impacts of parameter `-Z` for iperf3 measurements.
- [] Validate parameters for creating server and client processes