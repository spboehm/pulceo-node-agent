package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.*;
import dev.pulceo.pna.model.iperf.IperfResult;
import dev.pulceo.pna.model.jobs.*;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPResult;
import dev.pulceo.pna.model.nping.NpingUDPResult;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.model.resources.CPUUtilizationResult;
import dev.pulceo.pna.model.resources.MemoryUtilizationResult;
import dev.pulceo.pna.model.resources.NetworkUtilizationResult;
import dev.pulceo.pna.model.resources.StorageUtilizationResult;
import dev.pulceo.pna.repository.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Order(1)
@Service
public class JobService implements ManagedService {

    Logger logger = LoggerFactory.getLogger(JobService.class);

    @Autowired
    private BandwidthJobRepository bandwidthJobRepository;

    @Autowired
    private NpingTCPJobRepository npingTCPJobRepository;

    @Autowired
    private PingJobRepository pingJobRepository;

    @Autowired
    private LinkJobRepository linkJobRepository;

    @Autowired
    private NodeJobRepository nodeJobRepository;

    @Autowired
    private ResourceUtilizationJobRepository resourceUtilizationJobRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private IperfService iperfService;

    @Autowired
    private NpingService npingService;

    @Autowired
    private PingService pingService;

    @Autowired
    private ResourceUtilizationService resourceUtilizationService;

    // TODO: resolve that delayService and pingService is ambigious
    // TODO: consider renaming to job-related semantics
    @Autowired
    PublishSubscribeChannel npingUdpPubSubChannel;

    @Autowired
    PublishSubscribeChannel npingTcpPubSubChannel;

    // TODO: consider renaming to job-related semantics
    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    @Autowired
    PublishSubscribeChannel resourceUtilizationCPUServiceMessageChannel;

    private final Map<Long, ScheduledFuture<?>> bandwidthJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> TCPDelayJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> pingJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> jobHashMap = new ConcurrentHashMap<>();

    @Value("${pna.metrics.mqtt.topic}")
    private String metricsMqttTopic;

    @Value("${pna.health.mqtt.topic}")
    private String healthMqttTopic;

    @Value("${pna.uuid}")
    private String deviceId;

    @Value("${pna.delay.udp.data.length}")
    private String udpDataLength;

    public Optional<Job> readJob(long id) {
        return this.jobRepository.findById(id);
    }

    public Optional<NodeJob> readNodeJob(long id) throws JobServiceException {
        Optional<NodeJob> retrievedJob = this.nodeJobRepository.findById(id);
        if (retrievedJob.isPresent()) {
            return this.nodeJobRepository.findById(id);
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public ResourceUtilizationJob createNodeResourceUtilizationJob(ResourceUtilizationJob resourceUtilizationJob) {
        return this.resourceUtilizationJobRepository.save(resourceUtilizationJob);
    }

    public Optional<ResourceUtilizationJob> readNodeResourceUtilizationJob(Long id) {
        return this.resourceUtilizationJobRepository.findById(id);
    }

    public long scheduleResourceUtilizationJob(long id) {
        ResourceUtilizationJob retrievedResourceUtilizationJob = this.resourceUtilizationJobRepository.findById(id).get();
        switch (retrievedResourceUtilizationJob.getResourceUtilizationType()) {
            case CPU_UTIL:
                return scheduleResourceUtilizationJobForCPU(id);
            case MEM_UTIL:
                return scheduleResourceUtilizationJobForMEM(id);
            case NET_UTIL:
                return scheduleResourceUtilizationJobForNetwork(id);
            case STORAGE_UTIL:
                return scheduleResourceUtilizationJobForStorage(id);
            default:
                return 0L;
        }
    }

    public long scheduleResourceUtilizationJobForCPU(long id) {
        ResourceUtilizationJob retrievedResourceUtilizationJob = this.resourceUtilizationJobRepository.findById(id).get();
        long retrievedResourceUtilizationJobForCPUId = retrievedResourceUtilizationJob.getId();
        logger.info("Scheduling resource utilization job for CPU: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                CPUUtilizationResult cpuUtilizationResult = this.resourceUtilizationService.retrieveCPUUtilization(retrievedResourceUtilizationJob.getResourceUtilizationRequest());

                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(cpuUtilizationResult.getUuid())
                        .metricType(cpuUtilizationResult.getMetricType())
                        .jobUUID(retrievedResourceUtilizationJob.getUuid())
                        .metricResult(cpuUtilizationResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.resourceUtilizationCPUServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.debug("Sent message to resource utilization CPU service message channel: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
            } catch (ResourceServiceUtilizationException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedResourceUtilizationJob.getRecurrence()));
        this.jobHashMap.put(retrievedResourceUtilizationJobForCPUId, scheduledFuture);
        return retrievedResourceUtilizationJob.getId();
    }

    public long scheduleResourceUtilizationJobForMEM(long id) {
        ResourceUtilizationJob retrievedResourceUtilizationJob = this.resourceUtilizationJobRepository.findById(id).get();
        long retrievedResourceUtilizationJobForCPUId = retrievedResourceUtilizationJob.getId();
        logger.info("Scheduling resource utilization job for memory: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                MemoryUtilizationResult memoryUtilizationResult = this.resourceUtilizationService.retrieveMemoryUtilization(retrievedResourceUtilizationJob.getResourceUtilizationRequest());

                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(memoryUtilizationResult.getUuid())
                        .metricType(memoryUtilizationResult.getMetricType())
                        .jobUUID(retrievedResourceUtilizationJob.getUuid())
                        .metricResult(memoryUtilizationResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.resourceUtilizationCPUServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.debug("Sent message to resource utilization mem service channel: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
            } catch (ResourceServiceUtilizationException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedResourceUtilizationJob.getRecurrence()));
        this.jobHashMap.put(retrievedResourceUtilizationJobForCPUId, scheduledFuture);
        return retrievedResourceUtilizationJob.getId();
    }

    public long scheduleResourceUtilizationJobForNetwork(long id) {
        ResourceUtilizationJob retrievedResourceUtilizationJob = this.resourceUtilizationJobRepository.findById(id).get();
        long retrievedResourceUtilizationJobForCPUId = retrievedResourceUtilizationJob.getId();
        logger.info("Scheduling resource utilization job for network: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                NetworkUtilizationResult networkUtilizationResult = this.resourceUtilizationService.retrieveNetworkUtilizationResult(retrievedResourceUtilizationJob.getResourceUtilizationRequest());

                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(networkUtilizationResult.getUuid())
                        .metricType(networkUtilizationResult.getMetricType())
                        .jobUUID(retrievedResourceUtilizationJob.getUuid())
                        .metricResult(networkUtilizationResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.resourceUtilizationCPUServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.debug("Sent message to resource utilization CPU service message channel: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
            } catch (ResourceServiceUtilizationException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedResourceUtilizationJob.getRecurrence()));
        this.jobHashMap.put(retrievedResourceUtilizationJobForCPUId, scheduledFuture);
        return retrievedResourceUtilizationJob.getId();
    }

    public long scheduleResourceUtilizationJobForStorage(long id) {
        ResourceUtilizationJob retrievedResourceUtilizationJob = this.resourceUtilizationJobRepository.findById(id).get();
        long retrievedResourceUtilizationJobForCPUId = retrievedResourceUtilizationJob.getId();
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                StorageUtilizationResult storageUtilizationResult = this.resourceUtilizationService.retrieveStorageUtilizationResult(retrievedResourceUtilizationJob.getResourceUtilizationRequest());

                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(storageUtilizationResult.getUuid())
                        .metricType(storageUtilizationResult.getMetricType())
                        .jobUUID(retrievedResourceUtilizationJob.getUuid())
                        .metricResult(storageUtilizationResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.resourceUtilizationCPUServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.debug("Sent message to storage utilization CPU service message channel: " + retrievedResourceUtilizationJob.getResourceUtilizationRequest().getResourceName());
            } catch (ResourceServiceUtilizationException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedResourceUtilizationJob.getRecurrence()));
        this.jobHashMap.put(retrievedResourceUtilizationJobForCPUId, scheduledFuture);
        return retrievedResourceUtilizationJob.getId();
    }

    public Optional<LinkJob> readLinkJob(long id) throws JobServiceException {
        Optional<LinkJob> retrievedJob = this.linkJobRepository.findById(id);
        if (retrievedJob.isPresent()) {
            return retrievedJob;
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public long createNpingJob(NpingJob npingJob) {
        return this.npingTCPJobRepository.save(npingJob).getId();
    }

    public NpingJob readNpingJob(long id) throws JobServiceException {
        Optional<NpingJob> retrievedNpingTCPJob = this.npingTCPJobRepository.findById(id);
        if (retrievedNpingTCPJob.isPresent()) {
            return retrievedNpingTCPJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public ResourceUtilizationJob enableResourceUtilizationJob(long id) throws JobServiceException {
        Optional<ResourceUtilizationJob> retrievedResourceUtilizationJob = this.readNodeResourceUtilizationJob(id);
        if (retrievedResourceUtilizationJob.isPresent()) {
            if (!retrievedResourceUtilizationJob.get().isEnabled()) {
                retrievedResourceUtilizationJob.get().setEnabled(true);
                return this.resourceUtilizationJobRepository.save(retrievedResourceUtilizationJob.get());
            }
        }
        throw new JobServiceException("Requested job was not found!");
    }

    public NpingJob enableNpingJob(long id) throws JobServiceException {
        NpingJob retrievedNpingJob = this.readNpingJob(id);
        if (!retrievedNpingJob.isEnabled()) {
            retrievedNpingJob.setEnabled(true);
            return this.npingTCPJobRepository.save(retrievedNpingJob);
        }
        return retrievedNpingJob;
    }

    public NpingJob disableNpingJob(long id) throws JobServiceException {
        NpingJob retrievedNpingJob = this.readNpingJob(id);
        if (retrievedNpingJob.isEnabled()) {
            retrievedNpingJob.setEnabled(false);
            return this.npingTCPJobRepository.save(retrievedNpingJob);
        }
        return retrievedNpingJob;
    }

    // TODO: do not forget to set the status flag active
    // TODO: handle situation when the jobs are crashing
    public long scheduleNpingJob(long id) throws JobServiceException {
        NpingJob retrievedNpingJob = this.readNpingJob(id);
        long retrievedNpingJobId = retrievedNpingJob.getId();
        if (retrievedNpingJob.getNpingRequest().getNpingClientProtocol().equals(NpingClientProtocol.TCP)) {
            scheduleNpingTCPJob(retrievedNpingJob, retrievedNpingJobId);
        } else if (retrievedNpingJob.getNpingRequest().getNpingClientProtocol().equals(NpingClientProtocol.UDP)) {
            scheduleNpingUDPJob(retrievedNpingJob, retrievedNpingJobId);
        } else {
            throw new JobServiceException("NpingClientProtocol is not supported!");
        }
        return retrievedNpingJobId;
    }

    private void scheduleNpingUDPJob(NpingJob retrievedNpingJob, long retrievedNpingUDPJobId) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                NpingUDPResult npingUDPResult = npingService.measureUDPDelay(retrievedNpingJob.getNpingRequest().getDestinationHost(), Integer.parseInt(udpDataLength), retrievedNpingJob.getNpingRequest().getRounds(), retrievedNpingJob.getNpingRequest().getIface());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(npingUDPResult.getUuid())
                        .metricType(npingUDPResult.getMetricType())
                        .jobUUID(retrievedNpingJob.getUuid())
                        .metricResult(npingUDPResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.npingUdpPubSubChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
            } catch (DelayServiceException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedNpingJob.getRecurrence()));

        this.jobHashMap.put(retrievedNpingUDPJobId, scheduledFuture);
    }

    private void scheduleNpingTCPJob(NpingJob retrievedNpingJob, long retrievedNpingTCPJobId) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                NpingTCPResult npingTCPResult = npingService.measureTCPDelay(retrievedNpingJob.getNpingRequest().getDestinationHost(), retrievedNpingJob.getNpingRequest().getRounds(), retrievedNpingJob.getNpingRequest().getIface());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(npingTCPResult.getUuid())
                        .metricType(npingTCPResult.getMetricType())
                        .jobUUID(retrievedNpingJob.getUuid())
                        .metricResult(npingTCPResult)
                        .build();
                Message message = new Message(deviceId, networkMetric);
                this.npingTcpPubSubChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
            } catch (DelayServiceException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedNpingJob.getRecurrence()));

        this.jobHashMap.put(retrievedNpingTCPJobId, scheduledFuture);
    }

    // TODO: reimplement cancelNpingJob, consider active flag checked for unscheduling
    public boolean cancelNpingJob(long id) {
        return this.jobHashMap.get(id).cancel(false);
    }

    public long createIperfJob(IperfJob iperfJob) {
        return this.bandwidthJobRepository.save(iperfJob).getId();
    }

    public IperfJob readIperfJob(long id) throws JobServiceException {
        Optional<IperfJob> retrievedIperfJob = this.bandwidthJobRepository.findById(id);
        if (retrievedIperfJob.isPresent()) {
            return retrievedIperfJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public IperfJob enableIperfJob(long id) throws JobServiceException {
        IperfJob retrievedIperfJob = this.readIperfJob(id);
        if (!retrievedIperfJob.isEnabled()) {
            retrievedIperfJob.setEnabled(true);
            return this.bandwidthJobRepository.save(retrievedIperfJob);
        }
        return retrievedIperfJob;
    }

    public IperfJob disableIperfJob(long id) throws JobServiceException {
        IperfJob retrievedIperfJob = this.readIperfJob(id);
        if (retrievedIperfJob.isEnabled()) {
            retrievedIperfJob.setEnabled(false);
            return this.bandwidthJobRepository.save(retrievedIperfJob);
        }
        return retrievedIperfJob;
    }

    // TODO: do not forget to set the status flag active
    // TODO: handle situation when the jobs are crashing
    public long scheduleIperfJob(long id) throws JobServiceException {
        IperfJob retrievedIperfJob = this.readIperfJob(id);
        // TODO: Fix retrievedIperfJobId with UUID
        long retrievedIperfJobId = retrievedIperfJob.getId();
        logger.info("Scheduling iperf job: " + retrievedIperfJob.getIperfRequest().getDestinationHost());
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                if (retrievedIperfJob.getInitialDelay() > 0) {
                    logger.info("Initial delay: " + retrievedIperfJob.getInitialDelay() + " seconds");
                    Thread.sleep(retrievedIperfJob.getInitialDelay() * 1000L);
                }
                IperfResult iperfResult = iperfService.measureBandwidth(retrievedIperfJob.getIperfRequest());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(iperfResult.getUuid())
                        .metricType(iperfResult.getMetricType())
                        .jobUUID(retrievedIperfJob.getUuid())
                        .metricResult(iperfResult)
                        .build();
                Message message = new Message(deviceId, networkMetric);
                this.bandwidthServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.info("Sent message to bandwidth service message channel: " + retrievedIperfJob.getIperfRequest().getDestinationHost());
            } catch (BandwidthServiceException | InterruptedException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedIperfJob.getRecurrence()));
        this.jobHashMap.put(retrievedIperfJobId, scheduledFuture);
        return retrievedIperfJobId;
    }

    // TODO: reimplement cancelIperfJob, consider active flag
    public boolean cancelIperfJob(long id) {
        return this.jobHashMap.get(id).cancel(false);
    }

    // create
    public long createPingJob(PingJob pingJob) {
        return this.pingJobRepository.save(pingJob).getId();
    }

    // read
    public PingJob readPingJob(long id) throws JobServiceException {
        Optional<PingJob> retrievedPingJob = this.pingJobRepository.findById(id);
        if (retrievedPingJob.isPresent()) {
            return retrievedPingJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public Optional<PingJob> readPingJobOptional(long id) {
        return this.pingJobRepository.findById(id);
    }

    // enable
    public PingJob enablePingJob(long id) throws JobServiceException {
        PingJob retrievedPingJob = this.readPingJob(id);
        if (!retrievedPingJob.isEnabled()) {
            retrievedPingJob.setEnabled(true);
            return this.pingJobRepository.save(retrievedPingJob);
        }
        return retrievedPingJob;
    }

    // disable
    public PingJob disablePingJob(long id) throws JobServiceException {
        PingJob retrievedPingJob = this.readPingJob(id);
        if (retrievedPingJob.isEnabled()) {
            retrievedPingJob.setEnabled(false);
            return this.pingJobRepository.save(retrievedPingJob);
        }
        return retrievedPingJob;
    }

    public long schedulePingJob(long id) throws JobServiceException {
        PingJob retrievedPingJob = this.readPingJob(id);
        long retrievedPingJobId = retrievedPingJob.getId();
        logger.info("Scheduling ping job: " + retrievedPingJob.getPingRequest().getDestinationHost());
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                PingResult pingResult = pingService.measureRoundTripTime(retrievedPingJob.getPingRequest());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(pingResult.getUuid())
                        .metricType(pingResult.getMetricType())
                        .jobUUID(retrievedPingJob.getUuid())
                        .metricResult(pingResult)
                        .build();
                Message message = new Message(deviceId, networkMetric);
                this.pingServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
                logger.debug("Sent message to ping service message channel");
            } catch (PingServiceException e) {
                logger.error(e.getMessage());
            }
        }, Duration.ofSeconds(retrievedPingJob.getRecurrence()));
        this.jobHashMap.put(retrievedPingJobId, scheduledFuture);
        return retrievedPingJobId;
    }

    public void cancelJob(long id) {
        Optional<Job> jobToBeCancelled = this.readJob(id);
        if (jobToBeCancelled.isPresent()) {
            jobToBeCancelled.get().setEnabled(false);
            this.jobHashMap.get(id).cancel(false);
            this.jobRepository.save(jobToBeCancelled.get());
        }
    }

    public void deleteJobByUUID(UUID uuid) {
        Optional<Job> jobToBeDeleted = this.jobRepository.findByUuid(uuid);
        if (jobToBeDeleted.isPresent()) {
            this.cancelJob(jobToBeDeleted.get().getId());
            this.jobRepository.delete(jobToBeDeleted.get());
        }
    }

    public boolean cancelPingJob(long id) {
        return this.jobHashMap.get(id).cancel(false);
    }

    @Override
    public void reset() {
        // cancel all jobs
        for (Map.Entry<Long, ScheduledFuture<?>> entry : this.jobHashMap.entrySet()) {
            entry.getValue().cancel(false);
        }
        this.jobHashMap.clear();
        this.jobRepository.deleteAll();
    }

    @PostConstruct
    private void init() {
        Iterable<LinkJob> linkJobs = this.linkJobRepository.findByEnabled(true);
        linkJobs.forEach(linkJob -> {
            try {
                if (linkJob instanceof NpingJob) {
                    this.scheduleNpingJob(linkJob.getId());
                } else if (linkJob instanceof IperfJob) {
                    this.scheduleIperfJob(linkJob.getId());
                } else if (linkJob instanceof PingJob) {
                    this.schedulePingJob(linkJob.getId());
                } else {
                    throw new JobServiceException("Job type unknown!");
                }
            } catch (JobServiceException e) {
                logger.error("Could not init link jobs!");
                throw new RuntimeException("Could not init link jobs!", e);
            }
        });

        Iterable<NodeJob> nodeJobs = this.nodeJobRepository.findByEnabled(true);
        nodeJobs.forEach(nodeJob -> {
            try {
                if (nodeJob instanceof ResourceUtilizationJob) {
                    this.scheduleResourceUtilizationJob(nodeJob.getId());
                } else {
                    throw new JobServiceException("Job type unknown!");
                }
            } catch (JobServiceException e) {
                logger.error("Could not init node jobs!");
                throw new RuntimeException("Could not init node jobs!", e);

            }
        });
    }

    public Optional<ResourceUtilizationJob> readNodeResourceUtilizationJobByUUID(UUID uuid) {
        return this.resourceUtilizationJobRepository.findByUuid(uuid);
    }


}
