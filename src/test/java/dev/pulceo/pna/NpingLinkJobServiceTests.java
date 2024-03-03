package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.NpingJob;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingRequest;
import dev.pulceo.pna.model.nping.NpingTCPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;
import dev.pulceo.pna.repository.JobRepository;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.NpingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NpingLinkJobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    JobRepository jobRepository;

    @Autowired
    PublishSubscribeChannel npingUdpPubSubChannel;

    @Autowired
    PublishSubscribeChannel npingTcpPubSubChannel;

    @Autowired
    NpingService npingService;

    @Value("${pna.local.address}")
    private String localAddress;

    @BeforeEach
    @AfterEach
    public void killAllNpingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "nping").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
        this.jobRepository.deleteAll();
    }

    @Test
    public void testCreateNpingTCPJob() {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);

        // when
        long id  = this.jobService.createNpingJob(npingJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedNpingTCPJobIsInactive() throws JobServiceException {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);

        // when
        long savedIperfJob  = this.jobService.createNpingJob(npingJob);
        NpingJob retrievedNpingUDPJob = this.jobService.readNpingJob(savedIperfJob);

        // then
        assertFalse(retrievedNpingUDPJob.isEnabled());
    }

    @Test
    public void testEnableNpingTCPJobWithDisabledJob() throws JobServiceException {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);

        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingJob(npingJob);

        // when
        NpingJob enabledNpingJob = this.jobService.enableNpingJob(savedNpingTCPJobId);

        // then
        assertFalse(npingJob.isEnabled());
        assertTrue(enabledNpingJob.isEnabled());
    }

    @Test
    public void testEnableNpingTCPJobWithEnabledJob() throws JobServiceException {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        npingJob.setEnabled(true);
        long savedNpingTCPJobId = this.jobService.createNpingJob(npingJob);

        // when
        NpingJob alreadyEnabledNpingJob = this.jobService.enableNpingJob(savedNpingTCPJobId);

        // then
        assertTrue(npingJob.isEnabled());
        assertTrue(alreadyEnabledNpingJob.isEnabled());
    }

    @Test
    public void testDisableNpingTCPJobWithEnabledJob() throws JobServiceException {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        npingJob.setEnabled(true);
        long savedNpingTCPJobId = this.jobService.createNpingJob(npingJob);

        // when
        NpingJob enabledNpingJob = this.jobService.disableNpingJob(savedNpingTCPJobId);

        // then
        assertTrue(npingJob.isEnabled());
        assertFalse(enabledNpingJob.isEnabled());
    }


    @Test
    public void testDisableNpingTCPJobWithDisabledJob() throws JobServiceException {
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        // newly created job is disabled by default, means active = false
        long savedNpingTCPJobId = this.jobService.createNpingJob(npingJob);

        // when
        NpingJob alreadyDisabledNpingJob = this.jobService.disableNpingJob(savedNpingTCPJobId);

        // then
        assertFalse(npingJob.isEnabled());
        assertFalse(alreadyDisabledNpingJob.isEnabled());

    }

    @Test
    public void testScheduleNpingTCPJob() throws Exception {
        // given
        // prepare TCP listener on port 4002
        // implicitly done be SpringBootIntegration, see dev.pulceo.pna.config.TcpConfig
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        long id = this.jobService.createNpingJob(npingJob);

        // when
        long localJobId = this.jobService.scheduleNpingJob(id);
        BlockingQueue<Message> npingTCPResultBlockingQueue = new ArrayBlockingQueue<>(10);
        this.npingTcpPubSubChannel.subscribe(message -> npingTCPResultBlockingQueue.add((Message) message.getPayload()));
        this.jobService.cancelNpingJob(localJobId);
        Message message = npingTCPResultBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        NpingTCPDelayMeasurement npingTCPDelayMeasurement = (NpingTCPDelayMeasurement) map.get("npingTCPDelayMeasurement");

        // then
        assertNotNull(message);
        assert(localAddress.equals(map.get("sourceHost")));
        assert(localAddress.equals(map.get("destinationHost")));
        assertTrue(npingTCPDelayMeasurement.getMaxRTT() > 0);
        assertTrue(npingTCPDelayMeasurement.getMinRTT() > 0);
        assertTrue(npingTCPDelayMeasurement.getAvgRTT() > 0);
        assertEquals(1, npingTCPDelayMeasurement.getTcpConnectionAttempts());
        assertEquals(1, npingTCPDelayMeasurement.getTcpSuccessfulConnections());
        assertEquals(0, npingTCPDelayMeasurement.getTcpFailedConnectionsAbsolute());
        assertEquals(0, npingTCPDelayMeasurement.getTcpFailedConnectionsRelative());
    }

    @Test
    public void testScheduleNpingUdpJob() throws Exception {
        // given
        // prepare UDP listener on port 4001
        // implicitly done be SpringBootIntegration, see dev.pulceo.pna.config.UdpConfig
        // given
        NpingRequest npingRequest = new NpingRequest(localAddress, localAddress, 4001, NpingClientProtocol.UDP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        long id = this.jobService.createNpingJob(npingJob);

        // when
        long localJobId = this.jobService.scheduleNpingJob(id);
        BlockingQueue<Message> npingUdpResultBlockingQueue = new ArrayBlockingQueue<>(1);
        this.npingUdpPubSubChannel.subscribe(message -> npingUdpResultBlockingQueue.add((Message) message.getPayload()));
        this.jobService.cancelNpingJob(localJobId);
        Message message = npingUdpResultBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        NpingUDPDelayMeasurement npingUDPDelayMeasurement = (NpingUDPDelayMeasurement) map.get("npingUDPDelayMeasurement");

        // then
        assertNotNull(message);
        assert(localAddress.equals(map.get("sourceHost")));
        assert(localAddress.equals(map.get("destinationHost")));
        assertTrue(npingUDPDelayMeasurement.getMaxRTT() > 0);
        assertTrue(npingUDPDelayMeasurement.getMinRTT() > 0);
        assertTrue(npingUDPDelayMeasurement.getAvgRTT() > 0);
        assertEquals(1, npingUDPDelayMeasurement.getUdpPacketsSent());
        assertEquals(1, npingUDPDelayMeasurement.getUdpReceivedPackets());
        assertEquals(0, npingUDPDelayMeasurement.getUdpLostPacketsAbsolute());
        assertEquals(0.0, npingUDPDelayMeasurement.getUdpLostPacketsRelative());
    }

}
