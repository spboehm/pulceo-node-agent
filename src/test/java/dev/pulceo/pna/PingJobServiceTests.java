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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PingJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    @Autowired
    PingService pingService;

    private final String iface = "lo";

    @BeforeEach
    @AfterEach
    public void killAllPingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "ping").start();
        p.waitFor();
    }

    @Test
    public void testCreatePingJob() {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);

        // when
        long id = this.jobService.createPingJob(pingJob);

        // then
        assertTrue(id > 0);

    }

    // testCreatedNpingTCPJobIsInactive

    @Test
    public void testCreatedPingJobIsInactive() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);

        // when
        long savedPingJobId = this.jobService.createPingJob(pingJob);
        PingJob retrievedPingJob = this.jobService.readPingJob(savedPingJobId);

        // then
        assertFalse(retrievedPingJob.isEnabled());
    }

    @Test
    public void testEnablePingJobWithDisabledJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedPingJobId = this.jobService.createPingJob(pingJob);

        // when
        PingJob enabledPingJob = this.jobService.enablePingJob(savedPingJobId);

        // then
        assertFalse(pingJob.isEnabled());
        assertTrue(enabledPingJob.isEnabled());
    }

    @Test
    public void testEnablePingJobWithEnabledJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        pingJob.setEnabled(true);
        long savedPingJobId = this.jobService.createPingJob(pingJob);

        // when
        PingJob alreadyEnabledPingJob = this.jobService.enablePingJob(savedPingJobId);

        // then
        assertTrue(pingJob.isEnabled());
        assertTrue(alreadyEnabledPingJob.isEnabled());
    }

    @Test
    // given
    public void testDisablePingJobWithEnabledJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        pingJob.setEnabled(true);
        long savedPingJobId = this.jobService.createPingJob(pingJob);

        // when
        PingJob enabledPingJob = this.jobService.disablePingJob(savedPingJobId);

        // then
        assertTrue(pingJob.isEnabled());
        assertFalse(enabledPingJob.isEnabled());
    }

    @Test
    public void testDisablePingJobWithDisabledJob() throws JobServiceException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedPingJobId = this.jobService.createPingJob(pingJob);

        // when
        PingJob alreadyDisabledJob = this.jobService.disablePingJob(savedPingJobId);

        // then
        assertFalse(pingJob.isEnabled());
        assertFalse(alreadyDisabledJob.isEnabled());
    }

    // TODO: implement here
    @Test
    public void testSchedulePingJob() throws Exception {
        // given


        // when


        // then

    }

}
