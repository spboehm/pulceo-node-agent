package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import dev.pulceo.pna.repository.IperfResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class JobService {

    @Autowired
    private BandwidthService bandwidthService;

    @Autowired
    private BandwidthJobRepository jobRepository;

    @Autowired
    private IperfResultRepository iperfResultRepository;

    @Autowired
    private TaskScheduler taskScheduler;

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

    @Async
    public void scheduleIperfJob(long id) throws JobServiceException, ExecutionException, InterruptedException {
        IperfJob retrievedIperfJob = this.readIperfJob(id);
        taskScheduler.scheduleAtFixedRate(retrievedIperfJob.getIperfJobRunnable(), Duration.ofSeconds(retrievedIperfJob.getRecurrence()));
    }

}
