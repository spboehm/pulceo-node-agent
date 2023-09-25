package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfRequest;
import dev.pulceo.pna.model.iperf3.IperfResult;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.service.BandwidthService;
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
public class IperfJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    @Autowired
    BandwidthService bandwidthService;

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testCreateIperfJob() {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);

        // when
        long id  = this.jobService.createIperfJob(iperfJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedIperfJobIsInactive() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);

        // when
        long savedIperfJob = this.jobService.createIperfJob(iperfJob);
        IperfJob retrievedIperfJob = this.jobService.readIperfJob(savedIperfJob);

        // then
        assertFalse(retrievedIperfJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob enabledIperfJob = this.jobService.enableIperfJob(savedIperfJobId);

        // then
        assertFalse(iperfJob.isEnabled());
        assertTrue(enabledIperfJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        iperfJob.setEnabled(true);
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob alreadyEnabledIperfJob = this.jobService.enableIperfJob(savedIperfJobId);

        // then
        assertTrue(iperfJob.isEnabled());
        assertTrue(alreadyEnabledIperfJob.isEnabled());

    }

    @Test
    public void testDisableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        iperfJob.setEnabled(true);
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob enabledIperfJob = this.jobService.disableIperfJob(savedIperfJobId);

        // then
        assertTrue(iperfJob.isEnabled());
        assertFalse(enabledIperfJob.isEnabled());

    }

    @Test
    public void testDisableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob alreadyDisabledJob = this.jobService.disableIperfJob(savedIperfJobId);

        // then
        assertFalse(iperfJob.isEnabled());
        assertFalse(alreadyDisabledJob.isEnabled());

    }

    @Test
    public void testScheduleIperf3Job() throws Exception {
        // given
        int port = 5001;
        BandwidthServiceTests.startIperf3ServerInstance(port);
        int recurrence = 15;
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        long id = this.jobService.createIperfJob(iperfJob);

        // when
        long localJobId = this.jobService.scheduleIperfJob(id);
        BlockingQueue<IperfResult> iperfResultBlockingQueue = new ArrayBlockingQueue<>(1);
        this.bandwidthServiceMessageChannel.subscribe(message -> {
            iperfResultBlockingQueue.add((IperfResult) message.getPayload());
        });
        // initiate orderly shutdown
        this.jobService.cancelIperfJob(localJobId);
        IperfResult iperfResult = iperfResultBlockingQueue.take();

        // then
        assertNotNull(iperfResult);
        assert("localhost".equals(iperfResult.getSourceHost()));
        assert("localhost".equals(iperfResult.getDestinationHost()));
        assertTrue(iperfResult.getIperfBandwidthMeasurementReceiver().getBitrate() > 0);
        assertTrue(iperfResult.getIperfBandwidthMeasurementSender().getBitrate() > 0);
    }

}
