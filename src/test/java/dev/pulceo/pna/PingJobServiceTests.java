package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
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
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, iface);
        PingJob pingJob = new PingJob(pingRequest, 15);
        long pingJobId = this.jobService.createPingJob(pingJob);

        // when
        long scheduledPingJobId = this.jobService.schedulePingJob(pingJobId);
        BlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(1);
        this.pingServiceMessageChannel.subscribe(message -> messageBlockingQueue.add((Message) message.getPayload()));
        // initiate orderly shutdown
        this.jobService.cancelPingJob(scheduledPingJobId);
        Message message = messageBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        PingDelayMeasurement pingDelayMeasurement = (PingDelayMeasurement) map.get("pingDelayMeasurement");

        // then
        assertNotNull(message);
        assert("localhost".equals(map.get("sourceHost")));
        assert("localhost".equals(map.get("destinationHost")));
        assertEquals(1, pingDelayMeasurement.getPacketsTransmitted());
        assertTrue(pingDelayMeasurement.getPacketsReceived() >= 0);
        assertTrue(pingDelayMeasurement.getPacketLoss() >= 0.0);
        assertTrue(pingDelayMeasurement.getTime() >= 0);
        assertTrue(pingDelayMeasurement.getRttMin() >= 0);
        assertTrue(pingDelayMeasurement.getRttAvg() >= 0);
        assertTrue(pingDelayMeasurement.getRttMax() >= 0);
        assertTrue(pingDelayMeasurement.getRttMdev() >= 0);
    }

}
