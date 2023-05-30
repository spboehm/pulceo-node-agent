# TODOs

## BandwidthService 

- [] Implement dedicated port ranges for TCP and UDP bandwidth measurements, update `pna.iperf3.max.server.instances` accordingly.

## JobService

- [] Ensure that status flag of job is set properly in `scheduleIperfJob(...)`
- [] Ensure that all jobs are rescheduled after application crash
- [] Ensure that cancellation of jobs properly sets the active flag