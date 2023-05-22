package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.repository.BandwidthJobRepository;
import dev.pulceo.pna.repository.IperfResultRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

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

//    @Async
//    public Future<IperfResult> scheduleIperf3BandwidthJob(long id) throws Exception {
//        Optional<IperfJob> retrievedBandwidthJob = jobRepository.findById(id);
//        if (retrievedBandwidthJob.isPresent()) {
//            Runnable task = () -> {
//                try {
//                    IperfResult iperfResult = this.bandwidthService.measureBandwidth(retrievedBandwidthJob.get());
//                    IperfResult savedIperfResult = this.iperfResultRepository.save(iperfResult);
//                    System.out.println(savedIperfResult.toString());
//                } catch (BandwidthServiceException e) {
//                    throw new RuntimeException(e);
//                }
//            };
//            ScheduledFuture<?> iperfTaskScheduledFuture = taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(15));
//        } else {
//            throw new Exception("");
//        }
//    }

}
