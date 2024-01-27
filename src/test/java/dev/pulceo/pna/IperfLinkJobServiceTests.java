package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.iperf.IperfBandwidthMeasurement;
import dev.pulceo.pna.model.iperf.IperfClientProtocol;
import dev.pulceo.pna.model.iperf.IperfRequest;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.service.IperfService;
import dev.pulceo.pna.service.JobService;
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
public class IperfLinkJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    @Autowired
    IperfService iperfService;

    private final String bind = "localhost";

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
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);

        // when
        long id  = this.jobService.createIperfJob(iperfJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedIperfJobIsInactive() throws JobServiceException {
        // given
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
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
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
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
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
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
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
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
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
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
    public void testScheduleIperf3TCPJob() throws Exception {
        // given
        int port = 5001;
        IperfServiceTests.startIperf3ServerInstance(bind, port);
        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, bind);
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        long id = this.jobService.createIperfJob(iperfJob);

        // when
        long localJobId = this.jobService.scheduleIperfJob(id);
        BlockingQueue<Message> iperfResultBlockingQueue = new ArrayBlockingQueue<>(1);
        this.bandwidthServiceMessageChannel.subscribe(message -> {
            iperfResultBlockingQueue.add((Message) message.getPayload());
        });
        // initiate orderly shutdown
        this.jobService.cancelIperfJob(localJobId);
        Message message = iperfResultBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver = (IperfBandwidthMeasurement) map.get("iperfBandwidthMeasurementReceiver");
        IperfBandwidthMeasurement iperfBandwidthMeasurementSender = (IperfBandwidthMeasurement) map.get("iperfBandwidthMeasurementSender");

        // then
        assertNotNull(map);
        assert("localhost".equals(map.get("sourceHost")));
        assert("localhost".equals(map.get("destinationHost")));
        assertTrue(iperfBandwidthMeasurementReceiver.getBitrate() > 0);
        assertTrue(iperfBandwidthMeasurementSender.getBitrate() > 0);
    }

}
