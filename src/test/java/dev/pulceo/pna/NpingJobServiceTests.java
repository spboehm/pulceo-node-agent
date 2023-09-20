package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.NpingTCPJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.service.DelayService;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NpingJobServiceTests {

    @Autowired
    JobService jobService;

//    @Autowired
//    PublishSubscribeChannel bandwidthServiceMessageChannel;

    @Autowired
    DelayService delayService;

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
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);

        // when
        long id  = this.jobService.createNpingTCPJob(npingTCPJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedNpingTCPJobIsInactive() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);

        // when
        long savedIperfJob  = this.jobService.createNpingTCPJob(npingTCPJob);
        NpingTCPJob retrievedNpingUDPJob = this.jobService.readNpingTCPJob(savedIperfJob);

        // then
        assertFalse(retrievedNpingUDPJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);

        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob enabledNpingTCPJob = this.jobService.enableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertFalse(npingTCPJob.isEnabled());
        assertTrue(enabledNpingTCPJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);
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
    public void testDisableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);
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
    public void testDisableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        NpingTCPJob npingTCPJob = new NpingTCPJob("localhost", "localhost", 4001, NpingClientProtocol.TCP, 15);
        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingTCPJob(npingTCPJob);

        // when
        NpingTCPJob alreadyDisabledNpingTCPJob = this.jobService.disableNpingTCPJob(savedNpingTCPJobId);

        // then
        assertFalse(npingTCPJob.isEnabled());
        assertFalse(alreadyDisabledNpingTCPJob.isEnabled());

    }
//
//    @Test
//    public void testScheduleIperf3Job() throws Exception {
//        // given
//        int port = 5001;
//        BandwidthServiceTests.startIperf3ServerInstance(port);
//        int recurrence = 15;
//        IperfJob iperfJob = new IperfJob("localhost", "localhost", port, IperfClientProtocol.TCP, recurrence);
//        long id = this.jobService.createIperfJob(iperfJob);
//
//        // when
//        long localJobId = this.jobService.scheduleIperfJob(id);
//        BlockingQueue<IperfResult> iperfResultBlockingQueue = new ArrayBlockingQueue<>(1);
//        this.bandwidthServiceMessageChannel.subscribe(message -> {
//            iperfResultBlockingQueue.add((IperfResult) message.getPayload());
//        });
//        this.jobService.cancelIperfJob(localJobId);
//        IperfResult iperfResult = iperfResultBlockingQueue.take();
//
//        // then
//        assertNotNull(iperfResult);
//        assert("localhost".equals(iperfResult.getSourceHost()));
//        assert("localhost".equals(iperfResult.getDestinationHost()));
//        assertTrue(iperfResult.getIperfBandwidthMeasurementReceiver().getBitrate() > 0);
//        assertTrue(iperfResult.getIperfBandwidthMeasurementSender().getBitrate() > 0);
//    }

}
