package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class JobService {

    @Autowired
    private BandwidthJobRepository jobRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private BandwidthService bandwidthService;

    private final AtomicInteger atomicInteger = new AtomicInteger();

    private final Map<Integer, ScheduledFuture<?>> hashMap = new ConcurrentHashMap<Integer, ScheduledFuture<?>>();

    public long createIperfJob(IperfJob iperfJob) {
        return this.jobRepository.save(iperfJob).getId();
    }

    public IperfJob readIperfJob(long id) throws JobServiceException {
        Optional<IperfJob> retrievedIperfJob = this.jobRepository.findById(id);
        if (retrievedIperfJob.isPresent()) {
            return retrievedIperfJob.get();
        } else {
            throw new JobServiceException("Requested job was not found!");
        }
    }

    public long scheduleIperfJob(long id) throws JobServiceException {
        IperfJob retrievedIperfJob = this.readIperfJob(id);
        ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(() -> { bandwidthService.measureBandwidth(retrievedIperfJob); }, Duration.ofSeconds(retrievedIperfJob.getRecurrence()));
        int taskId = atomicInteger.getAndIncrement();
        this.hashMap.put(taskId, scheduledFuture);
        return taskId;
    }

    public boolean cancelIperfJob(Integer id) {
        this.hashMap.get(id).cancel(false);
        return true;
    }

}
