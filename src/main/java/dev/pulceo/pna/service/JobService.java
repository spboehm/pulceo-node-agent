package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.model.BandwidthJob;
import dev.pulceo.pna.model.iperf3.IperfResult;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import dev.pulceo.pna.repository.IperfResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

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

    public long createBandwidthJob(BandwidthJob bandwidthJob) {
        // TODO: check if already exists
        return this.jobRepository.save(bandwidthJob).getId();
    }

    public long createBandwidthResult(IperfResult iperfResult) {
        return this.iperfResultRepository.save(iperfResult).getId();
    }

    @Async
    public void scheduleIperf3BandwidthJob(long id) throws Exception {
        Optional<BandwidthJob> retrievedBandwidthJob = jobRepository.findById(id);
        if (retrievedBandwidthJob.isPresent()) {
            Runnable task = () -> {
                try {
                    IperfResult iperfResult = this.bandwidthService.measureBandwidth(retrievedBandwidthJob.get());
                    this.iperfResultRepository.save(iperfResult);
                } catch (BandwidthServiceException e) {
                    throw new RuntimeException(e);
                }
            };
            taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(retrievedBandwidthJob.get().getRecurrence()));
        } else {
            throw new Exception("");
        }
    }

}
