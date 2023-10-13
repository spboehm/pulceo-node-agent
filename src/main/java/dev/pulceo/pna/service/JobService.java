package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.model.iperf3.IperfResult;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.model.jobs.NpingTCPJob;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.nping.NpingTCPResult;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import dev.pulceo.pna.repository.NpingTCPJobRepository;
import dev.pulceo.pna.repository.PingJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.channel.PublishSubscribeChannel;
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
    private TaskScheduler taskScheduler;

    @Autowired
    private IperfService iperfService;

    @Autowired
    private NpingService npingService;

    @Autowired
    private PingService pingService;

    // TODO: consider renaming to job-related semantics
    @Autowired
    PublishSubscribeChannel delayServiceMessageChannel;

    // TODO: consider renaming to job-related semantics
    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    private final Map<Long, ScheduledFuture<?>> bandwidthJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> TCPDelayJobHashMap = new ConcurrentHashMap<>();
    private final Map<Long, ScheduledFuture<?>> pingJobHashMap = new ConcurrentHashMap<>();

    public long createNpingTCPJob(NpingTCPJob npingTCPJob) {
        return this.npingTCPJobRepository.save(npingTCPJob).getId();
    }

    public NpingTCPJob readNpingTCPJob(long id) throws JobServiceException {
        Optional<NpingTCPJob> retrievedNpingTCPJob = this.npingTCPJobRepository.findById(id);
        if (retrievedNpingTCPJob.isPresent()) {
            return retrievedNpingTCPJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public NpingTCPJob enableNpingTCPJob(long id) throws JobServiceException {
        NpingTCPJob retrievedNpingTCPJob = this.readNpingTCPJob(id);
        if (!retrievedNpingTCPJob.isEnabled()) {
            retrievedNpingTCPJob.setEnabled(true);
            return this.npingTCPJobRepository.save(retrievedNpingTCPJob);
        }
        return retrievedNpingTCPJob;
    }

    public NpingTCPJob disableNpingTCPJob(long id) throws JobServiceException {
        NpingTCPJob retrievedNpingTCPJob = this.readNpingTCPJob(id);
        if (retrievedNpingTCPJob.isEnabled()) {
            retrievedNpingTCPJob.setEnabled(false);
            return this.npingTCPJobRepository.save(retrievedNpingTCPJob);
        }
        return retrievedNpingTCPJob;
    }

    // TODO: do not forget to set the status flag active
    // TODO: handle situation when the jobs are crashing
    public long scheduleNpingTCPJob(long id) throws JobServiceException {
        NpingTCPJob retrievedNpingTCPJob = this.readNpingTCPJob(id);
        long retrievedNpingTCPJobId = retrievedNpingTCPJob.getId();
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                NpingTCPResult npingTCPResult = npingService.measureTCPDelay(retrievedNpingTCPJob.getDestinationHost());
                this.delayServiceMessageChannel.send(new GenericMessage<>(npingTCPResult));
            } catch (DelayServiceException e) {
                throw new RuntimeException(e);
            }
        }, Duration.ofSeconds(retrievedNpingTCPJob.getRecurrence()));

        this.TCPDelayJobHashMap.put(retrievedNpingTCPJobId, scheduledFuture);
        return retrievedNpingTCPJobId;
    }

    // TODO: reimplement cancelNpingJob, consider active flag
    public boolean cancelNpingTCPJob(long id) {
        return this.TCPDelayJobHashMap.get(id).cancel(false);
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
                // job.getObject
                IperfResult iperfResult = iperfService.measureBandwidth(retrievedIperfJob.getIperfRequest());
                this.bandwidthServiceMessageChannel.send(new GenericMessage<>(iperfResult));
            } catch (BandwidthServiceException e) {
                throw new RuntimeException(e);
            }

        }, Duration.ofSeconds(retrievedIperfJob.getRecurrence()));
        this.bandwidthJobHashMap.put(retrievedIperfJobId, scheduledFuture);
        return retrievedIperfJobId;
    }

    // TODO: reimplement cancelIperfJob, consider active flag
    public boolean cancelIperfJob(long id) {
        return this.bandwidthJobHashMap.get(id).cancel(false);
    }

    // create
    public long createPingJob(PingJob pingJob) {
        return this.pingJobRepository.save(pingJob).getId();
    }

    // read
    public  PingJob readPingJob(long id) throws JobServiceException {
        Optional<PingJob> retrievedPingJob = this.pingJobRepository.findById(id);
        if (retrievedPingJob.isPresent()) {
            return retrievedPingJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
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

    // schedule
    public long schedulePingJob(long id) throws JobServiceException {
        PingJob retrievedPingJob = this.readPingJob(id);
        long retrievedPingJobId = retrievedPingJob.getId();
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> {
            try {
                PingResult pingResult = pingService.measureRoundTripTime(retrievedPingJob.getPingRequest());
                //
            } catch (PingServiceException e) {
                throw new RuntimeException(e);
            }
        }, Duration.ofSeconds(retrievedPingJob.getRecurrence()));
        this.pingJobHashMap.put(retrievedPingJobId, scheduledFuture);
        return retrievedPingJobId;
    }
}
