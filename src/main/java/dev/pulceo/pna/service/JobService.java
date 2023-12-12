package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.model.iperf.IperfResult;
import dev.pulceo.pna.model.jobs.*;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPResult;
import dev.pulceo.pna.model.nping.NpingUDPResult;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class JobService {

    @Autowired
    private BandwidthJobRepository bandwidthJobRepository;

    @Autowired
    private NpingTCPJobRepository npingTCPJobRepository;

    @Autowired
    private PingJobRepository pingJobRepository;

    @Autowired
    private LinkJobRepository linkJobRepository;

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

    private final Map<Long, ScheduledFuture<?>> bandwidthJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> TCPDelayJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> pingJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> jobHashMap = new ConcurrentHashMap<>();

    @Value("${pna.metrics.mqtt.topic}")
    private String metricsMqttTopic;

    @Value("${pna.uuid}")
    private String deviceId;

    @Value("${pna.delay.udp.data.length}")
    private String udpDataLength;

    public Optional<Job> readJob(long id) {
        return this.jobRepository.findById(id);
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
                NpingUDPResult npingUDPResult = npingService.measureUDPDelay(retrievedNpingJob.getNpingRequest().getDestinationHost(), Integer.parseInt(udpDataLength));
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(npingUDPResult.getUuid())
                        .metricType(npingUDPResult.getMetricType())
                        .jobUUID(retrievedNpingJob.getUuid())
                        .metricResult(npingUDPResult)
                        .build();

                Message message = new Message(deviceId, networkMetric);
                this.npingUdpPubSubChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
            } catch (DelayServiceException e) {
                throw new RuntimeException(e);
            }
        }, Duration.ofSeconds(retrievedNpingJob.getRecurrence()));

        this.jobHashMap.put(retrievedNpingUDPJobId, scheduledFuture);
    }

    private void scheduleNpingTCPJob(NpingJob retrievedNpingJob, long retrievedNpingTCPJobId) {
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                NpingTCPResult npingTCPResult = npingService.measureTCPDelay(retrievedNpingJob.getNpingRequest().getDestinationHost());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(npingTCPResult.getUuid())
                        .metricType(npingTCPResult.getMetricType())
                        .jobUUID(retrievedNpingJob.getUuid())
                        .metricResult(npingTCPResult)
                        .build();
                Message message = new Message(deviceId, networkMetric);
                this.npingTcpPubSubChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
            } catch (DelayServiceException e) {
                throw new RuntimeException(e);
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
        long retrievedIperfJobId = retrievedIperfJob.getId();
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                IperfResult iperfResult = iperfService.measureBandwidth(retrievedIperfJob.getIperfRequest());
                NetworkMetric networkMetric = NetworkMetric.builder()
                        .metricUUID(iperfResult.getUuid())
                        .metricType(iperfResult.getMetricType())
                        .jobUUID(iperfResult.getUuid())
                        .metricResult(iperfResult)
                        .build();
                Message message = new Message(deviceId, networkMetric);
                this.bandwidthServiceMessageChannel.send(new GenericMessage<>(message, new MessageHeaders(Map.of("mqtt_topic", metricsMqttTopic))));
            } catch (BandwidthServiceException e) {
                throw new RuntimeException(e);
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
            } catch (PingServiceException e) {
                throw new RuntimeException(e);
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

    public boolean cancelPingJob(long id) {
        return this.jobHashMap.get(id).cancel(false);
    }
}
