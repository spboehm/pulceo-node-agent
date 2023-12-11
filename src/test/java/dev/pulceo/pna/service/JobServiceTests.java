package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.Job;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class JobServiceTests {

    @Autowired
    JobService jobService;

    @Test
    public void testCancelJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 5);
        long pingJobId = this.jobService.createPingJob(pingJob);
        this.jobService.schedulePingJob(pingJobId);

        // when
        this.jobService.cancelJob(pingJobId);

        // then
        Optional<Job> cancelledJob = this.jobService.readJob(pingJobId);
        if (cancelledJob.isPresent()) {
            Job job = cancelledJob.get();
            assertFalse(job.isEnabled());
        }
    }
}
