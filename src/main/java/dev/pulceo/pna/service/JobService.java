package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.BandwidthJob;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobScheduler jobScheduler;

    @Autowired
    private BandwidthService bandwidthService;

    @Autowired
    private BandwidthJobRepository jobRepository;

    public long createJob (BandwidthJob bandwidthJob) {
        // TODO: check if already exists
        return this.jobRepository.save(bandwidthJob).getId();
    }

    public void scheduleIperf3BandwidthJob(long id) throws JobServiceException {
        Optional<BandwidthJob> retrievedBandwidthJob = jobRepository.findById(id);
        if (retrievedBandwidthJob.isPresent()) {
            jobScheduler.scheduleRecurrently(Cron.every30seconds(), () -> {
                bandwidthService.measureBandwidth(retrievedBandwidthJob.get());
            });
        } else {
            throw new JobServiceException("");
        }
    }

}
