package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.NpingTCPJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPResult;
import dev.pulceo.pna.service.NpingService;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NpingJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PublishSubscribeChannel delayServiceMessageChannel;

    @Autowired
    NpingService npingService;

    @BeforeEach
    @AfterEach
    public void killAllNpingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "nping").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testCreateNpingTCPJob() {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);

        // when
        long id  = this.jobService.createNpingTCPJob(npingTCPJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedNpingTCPJobIsInactive() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);

        // when
        long savedIperfJob  = this.jobService.createNpingTCPJob(npingTCPJob);
        NpingTCPJob retrievedNpingUDPJob = this.jobService.readNpingTCPJob(savedIperfJob);

        // then
        assertFalse(retrievedNpingUDPJob.isEnabled());
    }

    @Test
    public void testEnableNpingTCPJobWithDisabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);

        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob enabledNpingTCPJob = this.jobService.enableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertFalse(npingTCPJob.isEnabled());
        assertTrue(enabledNpingTCPJob.isEnabled());
    }

    @Test
    public void testEnableNpingTCPJobWithEnabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        npingTCPJob.setEnabled(true);
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob alreadyEnabledNpingTCPJob = this.jobService.enableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertTrue(npingTCPJob.isEnabled());
        assertTrue(alreadyEnabledNpingTCPJob.isEnabled());
    }

    @Test
    public void testDisableNpingTCPJobWithEnabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        npingTCPJob.setEnabled(true);
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob enabledNpingTCPJob = this.jobService.disableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertTrue(npingTCPJob.isEnabled());
        assertFalse(enabledNpingTCPJob.isEnabled());
    }


    @Test
    public void testDisableNpingTCPJobWithDisabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, 15);
        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob alreadyDisabledNpingTCPJob = this.jobService.disableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertFalse(npingTCPJob.isEnabled());
        assertFalse(alreadyDisabledNpingTCPJob.isEnabled());

    }

    @Test
    public void testScheduleNpingTCPJob() throws Exception {
        // given
        // prepare TCP listener on port 4002
        // implicitly done be SpringBootIntegration, see dev.pulceo.pna.config.TcpConfig
        int recurrence = 15;
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4002, NpingClientProtocol.TCP, recurrence);
        long id = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        long localJobId = this.jobService.scheduleNpingTCPJob(id);
        BlockingQueue<NpingTCPResult> npingTCPResultBlockingQueue = new ArrayBlockingQueue<>(1);
        this.delayServiceMessageChannel.subscribe(message -> {
            npingTCPResultBlockingQueue.add((NpingTCPResult) message.getPayload());
        });
        this.jobService.cancelNpingTCPJob(localJobId);
        NpingTCPResult npingTCPResult = npingTCPResultBlockingQueue.take();

        // then
        assertNotNull(npingTCPResult);
        assert("localhost".equals(npingTCPResult.getSourceHost()));
        assert("localhost".equals(npingTCPResult.getDestinationHost()));
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getMaxRTT() > 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getMinRTT() > 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getAvgRTT() > 0);
        assertEquals(1, npingTCPResult.getNpingTCPDelayMeasurement().getTcpConnectionAttempts());
        assertEquals(1, npingTCPResult.getNpingTCPDelayMeasurement().getTcpSuccessfulConnections());
        assertEquals(0, npingTCPResult.getNpingTCPDelayMeasurement().getTcpFailedConnectionsAbsolute());
        assertEquals(0, npingTCPResult.getNpingTCPDelayMeasurement().getTcpFailedConnectionsRelative());
    }

}
