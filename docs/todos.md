# TODOs

## Data model

- [ ] Add CreatedDate
- [ ] Add CreatedBy
- [ ] Add LastModifiedDate
- [ ] Add LastModifiedBy
- [ ] Add UUID
- [ ] Nodes - ensure that the local node-idea is properly injected
- [ ] Check `JPA mappings` in all `model` classes
- [ ] Consider adding name of device to `Node` class
- [ ] Consider changing the attribute names for the json serialization
- [ ] Introduce Version for all entities
- [ ] Consider renaming `Jobs` to `MetricRequests` or `MeasurementRequests`
- [ ] Consider adding `@Builder` for all 
- [ ] Check all set UUIDs in the project
- [ ] Consider using Spring HATEOS for all Outgoing DTOs
- [ ] Repository classes in tests should manage the lifecycle of the entities
- [ ] Enable proper bean validation for all DTOs
- [ ] Make sure defaults are set for all DTOs at one proper location
- [ ] `ShortRequestDto` should be clear about the `UUID` and the mapping either to type `metric` or ``

## Overall Issues

- [ ] Inconsistent naming of bandwidth vs. iperf, e.g. in (`JobService`)
- [ ] Inconsistent naming of delay vs. npingTCP, e.g. in (`JobService`)
- [ ] Implement support for IPv6 
- [ ] SourceHost should be removed from `PingRequest`, `IperfRequest`, and `NpingReqeuest` 
- [ ] Consider using `Optional` for all read operations in service classes
- [ ] Consider returning the Object in all service classes for create operations
  - [ ] `NodeService`
  - 
- [ ] `Long` vs. `long`
- [ ] Consider removing inherit `Repositories` in favor of `JobRepository`
- [ ] Properly add `@Autowired` bean configuration
- [ ] source and destination host should be removed from all Results and rather be added to the node.
- [ ] Implement builder pattern for all model classes
- [ ] Set cascade type for @OneToOne relationships in `Link` class
- [ ] Revise if some resources can be emdedded, e.g, `Requests` in `Jobs` for less database transactions

## MQTT Endpoint

- [ ] Shorten messages for "icmp-rtt", "tcp-bw", and "udp-bw" and so on, introduce a long and short version

## REST API

- [ ] Adjust DefaultHandlerExceptionResolver, e.g., for methods that are not defined. Do not return stack trace.
- [ ] Also for Bad Request (400), if something is missing
- [ ] Consider parsing response body for failed requests in MockMvc 

## Package config

- [ ] Replace UdpEchoServer with traditional Java-based configuration
- [ ] Revise all configurations, esp. `TcpConfig`, `UdpConfig`, `MQTTConfig`, and `DelayServiceConfig`

## All network-related services

- [ ] Set the network interfaces appropriately in all `Request` classes

## All service tests

- [ ] Check order of expected and actual

## InitPulceoNodeAgentBean

- [ ] Check if it is better to return `boolean` instead of `void` in `init()`
- [ ] Consider moving the registration process with id and token to the database and a service
- [ ] Redesign the pna.id generation process, do not rely on pna.id in application.properties

## ModelMapperConfig

- [ ] Check if `@Configuration` is required

## Cloud RegistrationService

- [ ] Create different `application.properties` for different ways to create tokens
- [ ] Consider Hashing the generated token
- [ ] put initTokenMethod with private access modifier
- [ ] Consider putting the init token at a different location in a simple text file to avoid pushing it to remote filesystem
- [ ] Consider removing the init bean in favor of a service
- [ ] Consider that generated Values are matched in test classes
- [ ] Intensify mock verification options, verify no more interactions and consider comparing parameters for `PnaInitToken`

## NodeService

- [ ] Prevent creating duplicates of Nodes
- [ ] Add different strategies for adding a node
  - [ ] Just by stating the endpoint (e.g., IP address) and an API-Token
  - [ ] By using cloud providers with bootstrapping particular nodes
- [x] Consider adding additional methods for initializing a node
- [ ] Consider renaming `pnaId` to `pnaUUID`

## LinkService

- [ ] Prevent creating duplicated of Links
- [ ] Check if there are proper references between `Link`s and `Job`s
- [ ] Properly cross references between `Job`s and `Link`s 
- [ ] Check the dependency of `Jobs` and `Links`

## BandwidthService 

- [ ] Implement dedicated port ranges for TCP and UDP bandwidth measurements, update `pna.iperf3.max.server.instances` accordingly
- [ ] Reference defined port ranges in `BandwidthService`
- [x] Implement test with only one measurement result, otherwise tests last pretty long
- [ ] Check client and sender semantics, respectively naming
- [x] Ensure that particular interfaces can be used for measuring bandwidth
- [x] Improve process generation with ProcessBuilder
- [ ] Set minimum values for recurrence
- [ ] IMPORTANT: Set value for recurrence appropriately, e.g., 5M, 5S
- [x] Remove Job semantics for `BandwidthJob`
- [x] Add `IperfRequest` instead of `IperfJob`
- [x] Implement `testMeasureBandwidth()` in `BandwidthServiceTests`
- [ ] Also include the retr tansmission in iperf3
- [x] Include custom bitrate in iperf3
- [ ] Add object-oriented style for running 
- [x] Add test cases for TCP
- [ ] Add appropriate validation for all input parameters of BandwidthService
  - [ ] In UDP mode, bitrate must be 1 at least
  - [ ] In TCP mode, bitrate can be 0
- [ ] Ensure that `IperfBandwidthMeasurement` has the right inheritance
- [ ] Rework waiting on starting process in `measure...()`
- [ ] !!! Provide possibility to set the target port dynamically by asking the source node for the next free port, do this dynamically in `LinkController`
- [ ] Structure of Inheritance between `IperfBandwidthMeasurement` and `IperfUDPBandwidthMeasurement` is unclear and hard to handle 

## DelayService

- [ ] Ensure that multiple backends are available for determining the latency, example (`nping` and `ping`)
- [ ] Ensure that different rounds can be defined for determining the latency
- [x] Ensure that the payload size can be specified for the latency test, at least for TCP
- [ ] Refactor `measureDelay()`, improve handling
- [ ] Improve process generation with ProcessBuilder
- [ ] Set minimum values for recurrence
- [ ] Remove Job semantics for `NpingJob`
- [x] Add `NpingRequest` instead of `NpingJob`
- [ ] Ensure that no duplicate measurements can be created
- [x] Remove port from all methods
- [x] Add test cases for UDP
- [ ] Ensure that the inheritance is correctly chosen
- [ ] Rework waiting on starting process in `measure...()`
- [x] Rename `NpingTCPJob` to `NpingJob`
- [ ] Recurrence only in seconds, allow for modification in minutes or hours
- [ ] Maybe add a sequence number for the delay measurement

## PingService

- [ ] Rework waiting on starting process in `measure...()`

## JobService

- [ ] Ensure that status flag of linkJob is set properly in `scheduleIperfJob(...)`
- [ ] Ensure that all jobs are rescheduled after application crash
- [ ] Ensure that cancellation of jobs properly sets the active flag
- [x] Enable that the result is sent via `this.delayServiceMessageChannel.send(new GenericMessage<>(npingTCPResult))`;
- [ ] Ensure that no duplicate jobs can be created
- [ ] Add appropriate logging for 
- [ ] Consider moving PublishSubscribeChannels for bandwidth and delay to another place
- [ ] Consider working with `Optional` for all read operations
- [ ] Check if a (Network) Job can be created even without being attached to a link
- [ ] Check if there are cross references between Jobs and links
- [ ] `JObService#scheduleIperfJob` should completely encapsulate `IperfResult`, not a map-like structure that ends up in not proper mappings  
- [ ] Ensure that cancellation and disabling of jobs is properly handeled with an additional parameter `true` or `false`
- [ ] Ensure that cancellation and disabling is properly set in other methods in `JobService`
- [ ] Clean `JobService` with concrete methods
- [ ] Ensure that on startup, alle enabled jobs are loaded into the `JobHashMap` 

## NpingUtils

- [ ] Run tests with different output parameters, in case output is falsely parsed
- [ ] Ensure that particular interfaces can be used for determining the delay, e.g., via env vars
- [ ] Clarify method signature of `extractNpingTCPDelayMeasurement(...)`... First parameter is currently obsolete
- [x] Improve command generation out of `NpingCmd` class, avoid error-prone hard-coding
- [ ] Add dataLength validation in NpingCmd and respective tests

## Iperf3utils

- [ ] Check for performance impacts of parameter `-Z` for iperf3 measurements.
- [ ] Validate parameters for creating server and client processes
- [ ] Run tests withs different output parameters, in case output is falsely parsed
- [ ] Check if output is correctly processed even if iperf has failed to parse the output
- [ ] Check client and sender semantics, respectively rename sender and receiver because it is ambiguous
- [x] Ensure that particular interfaces can be determined for measuring the bandwidth, e.g., via env vars
- [x] Improve command generation out of `IperfCmd` class
- [x] Add additional test cases for UDP
- [x] Add additional test cases for integer results, case TCP
- [ ] Check if additional tests for isTCPSender / testIsUDPSender are required
- [x] Rename all `--bind-dev` tp `--bind` in `IperfCmd` and other classes

## PingUtils

- [ ] Check error handling of PingUtils in case of `name or service not found`, might not be good idea to pass the output of the nping proces to the user

## Helper classes (package `utils`)

- [ ] Refactor int and float parsing methods in `NpingUtils`, `Iperf3Utils`, `PingUtils`

## Application Startup

- [ ] Connection probe MQTT
- [ ] Existence of nping and iperf3 executable
- [ ] Disable Devtools by setting 'spring.devtools.add-properties' to false
- [ ] Ensure that device id is properly set (`pna.id`)
- [ ] Ensure that the device name is properly set (`pna.name`)
- [ ] Ensure that all low level tools a function properly, e.g., `ping`, `nping`, `iperf3`, by doing a short check during the first startup

## Join Process

- [ ] Via PNA-node-agent: Nodes is booting and showing connection string with `endpoint`, `port`, and `token`. Cloud can then authenticate to the device and register.
- [ ] Via IaaS: All basic information is filled, endpoint, port, and token are generated and passed to the pna-agent. Cloud can then authenticate to the device because it is registered in the cloud.

## Miscellaneous

- [x] ensure that osiv is set to false

## Tests

- [ ] `dev.pulceo.pna.IperfLinkJobServiceTests` misses additional tests for linkJob cancellation
- [ ] `dev.pulceo.pna.NpingLinkJobServiceTests` misses additional tests for linkJob cancellation
- [ ] `BandwidthServiceTests#startIperf3TCPSenderInstance` uses a too long sending interval. Reduce it. 
- [ ] add additional error cases of failed Iperf3 Measurements
- [x] Add missing tests for `PingJobServiceTests`
- [ ] `LinkServiceUnitTests` misses proper configuration of `properties`. Consider excluding external dependencies completely.
- [ ] Consider renaming `read` to `retrieved`, e.g., in `LinkService`
- [ ] Fix port collision for alle `@AutoConfigureMockMvc` tests

## CloudRegistrationControllerTests

- [ ] Revise if `@SpringBootTest` is required, because it creates a new application context (https://docs.spring.io/spring-framework/reference/testing/spring-mvc-test-framework/server-setup-options.html)

## Package config

- [ ] Adjust naming of variables inside configuration classes

## LinkController

- [ ] Add own controller for `metric-requests`
- [ ] Refactor properties mapping to own method
