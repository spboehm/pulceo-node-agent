package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.PingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class PingJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    @Autowired
    PingService pingService;

    @BeforeEach
    @AfterEach
    public void killAllPingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "ping").start();
        p.waitFor();
    }

    @Test
    public void testCreatePingJob() {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 15);

        // when
        long id = this.jobService.createPingJob(pingJob);

        // then
        assertTrue(id > 0);

    }

    // testCreatedNpingTCPJobIsInactive

    @Test
    public void testCreatedPingJobisInactive() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 15);

        // when
        long savedPingJob = this.jobService.createPingJob(pingJob);
        PingJob retrievedPingJob = this.jobService.readPingJob(savedPingJob);

        // then
        assertFalse(retrievedPingJob.isEnabled());
    }

    @Test
    public void testEnablePingJobWithDisabledJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedPingJob = this.jobService.createPingJob(pingJob);

        // when
        PingJob enabledPingJob = this.jobService.enablePingJob(savedPingJob);

        // then
        assertFalse(pingJob.isEnabled());
        assertTrue(enabledPingJob.isEnabled());

    }

    // testEnableNpingTCPJobWithDisabledJob

    // testEnableNpingTCPJobWithEnabledJob

    // testDisableNpingTCPJobWithEnabledJob

    // testDisableNpingTCPJobWithDisabledJob

    // testScheduleNpingTCPJob

}
