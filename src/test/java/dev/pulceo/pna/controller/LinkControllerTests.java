package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.metricrequests.*;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.LinkDTOUtil;
import dev.pulceo.pna.dtos.MetricRequestDTOUtil;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import dev.pulceo.pna.model.iperf.IperfClientProtocol;
import dev.pulceo.pna.model.iperf.IperfRole;
import dev.pulceo.pna.model.iperf.IperfUDPBandwidthMeasurement;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.nping.NpingTCPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.repository.LinkRepository;
import dev.pulceo.pna.repository.NodeRepository;
import dev.pulceo.pna.service.IperfService;
import dev.pulceo.pna.service.NodeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=8002", "pna.delay.udp.port=8003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440005"})
@AutoConfigureMockMvc(addFilters = false)
public class LinkControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private NodeService nodeService;

    @Autowired
    private IperfService iperfService;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    @Autowired
    PublishSubscribeChannel npingUdpPubSubChannel;

    @Autowired
    PublishSubscribeChannel npingTcpPubSubChannel;

    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pna.uuid}")
    private String pnaUuid;

    @Value("${pna.local.address}")
    private String localAddress;

    // TODO: remove this workaround after properly setting up iperf3 server start and stop
    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @BeforeEach
    public void setUp() {
        linkRepository.deleteAll();
    }

    @Test
    public void testCreateLink() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        Optional<Node> localNode = this.nodeService.readLocalNode();
        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(String.valueOf(localNode.get().getUuid()), nodeUuid);
        String createNewLinkDTOAsJson = this.objectMapper.writeValueAsString(createNewLinkDTO);

        // when and then
        this.mockMvc.perform(post("/api/v1/links")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(createNewLinkDTOAsJson))
                .andExpect(status().isCreated());
    }

    @Test
    public void testCancelMetricRequest() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        String linkUuid = createNewTestLink(nodeUuid);

        CreateNewMetricRequestIcmpRttDTO createNewMetricRequestIcmpRttDTO = MetricRequestDTOUtil.createIcmpRttMetricRequestDTO("icmp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestIcmpRttDTO);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/icmp-rtt-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                .andExpect(status().isOk())
                .andReturn();
        String metricRequestUuid = objectMapper.readTree(metricRequestResult.getResponse().getContentAsString()).get("remoteLinkUUID").asText();

        // when
        cancelMetricRequest(linkUuid, metricRequestUuid);

        // then
        // TODO: perform another get request to check if the metric request is disabled
    }

    private void cancelMetricRequest(String linkUuid, String metricRequestUuid) throws Exception {
        PatchMetricDto patchMetricDto = PatchMetricDto.builder().enabled(false).build();
        String patchMetricDtoAsJson = objectMapper.writeValueAsString(patchMetricDto);
        this.mockMvc.perform(patch("/api/v1/links/" + linkUuid + "/metric-requests/" + metricRequestUuid)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(patchMetricDtoAsJson))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testNewICMPRTTRequest() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        String linkUuid = createNewTestLink(nodeUuid);

        // when and then
        CreateNewMetricRequestIcmpRttDTO createNewMetricRequestIcmpRttDTO = MetricRequestDTOUtil.createIcmpRttMetricRequestDTO("icmp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestIcmpRttDTO);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/icmp-rtt-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                        .andExpect(status().isOk())
                        .andReturn();
        String metricRequestUuid = objectMapper.readTree(metricRequestResult.getResponse().getContentAsString()).get("remoteLinkUUID").asText();
        cancelMetricRequest(linkUuid, metricRequestUuid);
        // TODO: do validation here of MetricRequestDTO
        // TODO: check for new linkUUID

        // wait for icmp-rtt value
        BlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(1);
        this.pingServiceMessageChannel.subscribe(message -> {
            Message payload = (Message) message.getPayload();

        NetworkMetric networkMetric = (NetworkMetric) payload.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        PingDelayMeasurement pingDelayMeasurement = (PingDelayMeasurement) map.get("pingDelayMeasurement");

        // then
        // TODO: refactor and expand the test to other metric values
        assertNotNull(message);
        assert(localAddress.equals(map.get("sourceHost")));
        assert(localAddress.equals(map.get("destinationHost")));
        assertEquals(1, pingDelayMeasurement.getPacketsTransmitted());
        assertTrue(pingDelayMeasurement.getPacketsReceived() >= 0);
        assertTrue(pingDelayMeasurement.getPacketLoss() >= 0.0);
        assertTrue(pingDelayMeasurement.getTime() >= 0);
        assertTrue(pingDelayMeasurement.getRttMin() >= 0);
        assertTrue(pingDelayMeasurement.getRttAvg() >= 0);
        assertTrue(pingDelayMeasurement.getRttMax() >= 0);
        assertTrue(pingDelayMeasurement.getRttMdev() >= 0);
        });

    }

    @Test
    public void testNewUdpRttRequest() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        String linkUuid = createNewTestLink(nodeUuid);

        // when and then
        CreateNewMetricRequestUdpRttDto createNewMetricRequestUdpRttDto = MetricRequestDTOUtil.createUdpRttMetricRequestDTO("udp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestUdpRttDto);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/udp-rtt-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                        .andExpect(status().isOk())
                        .andReturn();
        String metricRequestUuid = objectMapper.readTree(metricRequestResult.getResponse().getContentAsString()).get("remoteLinkUUID").asText();
        cancelMetricRequest(linkUuid, metricRequestUuid);

        // TODO: do validation here of MetricRequestDTO

        // wait for udp-rtt value
        BlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(5);
        this.npingUdpPubSubChannel.subscribe(message -> {
            Message payload = (Message) message.getPayload();

            NetworkMetric networkMetric = (NetworkMetric) payload.getMetric();
            Map<String, Object> map = networkMetric.getMetricResult().getResultData();
            NpingUDPDelayMeasurement npingUDPDelayMeasurement = (NpingUDPDelayMeasurement) map.get("npingUDPDelayMeasurement");

            // then
            assertNotNull(message);
            assert (localAddress.equals(map.get("sourceHost")));
            assert (localAddress.equals(map.get("destinationHost")));
            assertTrue(npingUDPDelayMeasurement.getMaxRTT() > 0);
            assertTrue(npingUDPDelayMeasurement.getMinRTT() > 0);
            assertTrue(npingUDPDelayMeasurement.getAvgRTT() > 0);
            assertEquals(1, npingUDPDelayMeasurement.getUdpPacketsSent());
            assertEquals(1, npingUDPDelayMeasurement.getUdpReceivedPackets());
            assertEquals(0, npingUDPDelayMeasurement.getUdpLostPacketsAbsolute());
            assertEquals(0.0, npingUDPDelayMeasurement.getUdpLostPacketsRelative());
        });
    }

    @Test
    public void testNewTcpRttRequest() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        String linkUuid = createNewTestLink(nodeUuid);

        // when and then
        CreateNewMetricRequestTcpRttDto createNewMetricRequestTcpRttDto = MetricRequestDTOUtil.createNewMetricRequestTcpRttDto("tcp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestTcpRttDto);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/tcp-rtt-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                .andExpect(status().isOk())
                .andReturn();
        String metricRequestUuid = objectMapper.readTree(metricRequestResult.getResponse().getContentAsString()).get("remoteLinkUUID").asText();
        cancelMetricRequest(linkUuid, metricRequestUuid);
        // TODO: do validation here of MetricRequestDTO

        // wait for tcp-rtt value
        this.npingTcpPubSubChannel.subscribe(message -> {
            Message payload = (Message) message.getPayload();

        NetworkMetric networkMetric = (NetworkMetric) payload.getMetric();
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
        });
    }

    @Test
    public void testNewUdpBwRequest() throws Exception {
        // given
        String nodeUuid = createNewTestDestNode();
        String linkUuid = createNewTestLink(nodeUuid);

        // then and then
        CreateNewMetricRequestUdpBwDto createNewMetricRequestUdpBwDto = MetricRequestDTOUtil.createNewMetricRequestUdpBwDto("udp-bw");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestUdpBwDto);
        // TODO: start the iperf3 server on remote instance, but this is just a workaround, must be triggered by pna or prm via API requests
        this.iperfService.startIperf3Server();
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/udp-bw-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                        .andExpect(status().isOk())
                        .andReturn();
        String metricRequestUuid = objectMapper.readTree(metricRequestResult.getResponse().getContentAsString()).get("remoteLinkUUID").asText();
        cancelMetricRequest(linkUuid, metricRequestUuid);

        // TODO: do validation here of MetricRequestDTO

        // wait for udp-bw value
        BlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(100);
        this.bandwidthServiceMessageChannel.subscribe(message -> messageBlockingQueue.add((Message) message.getPayload()));
            Message message = messageBlockingQueue.take();
            NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
            Map<String, Object> map = networkMetric.getMetricResult().getResultData();
            IperfUDPBandwidthMeasurement iperfUDPBandwidthMeasurementSender = (IperfUDPBandwidthMeasurement) map.get("iperfBandwidthMeasurementSender");
            IperfUDPBandwidthMeasurement iperfUDPBandwidthMeasurementReceiver = (IperfUDPBandwidthMeasurement) map.get("iperfBandwidthMeasurementReceiver");

            // then
            assertEquals(localAddress, map.get("sourceHost"));
            assertEquals(localAddress, map.get("destinationHost"));
            // Sender
            assertEquals(IperfClientProtocol.UDP, iperfUDPBandwidthMeasurementSender.getIperf3Protocol());
            assertTrue(iperfUDPBandwidthMeasurementSender.getBitrate() > 0);
            assertEquals("Mbits/s", iperfUDPBandwidthMeasurementSender.getBandwidthUnit());
            assertEquals(IperfRole.SENDER, iperfUDPBandwidthMeasurementSender.getIperfRole());

            assertTrue(iperfUDPBandwidthMeasurementSender.getJitter() >= 0.00f);
            assertTrue(iperfUDPBandwidthMeasurementSender.getLostDatagrams() >= 0);
            assertTrue(iperfUDPBandwidthMeasurementSender.getTotalDatagrams() > 0);

            // Receiver
            assertEquals(IperfClientProtocol.UDP, iperfUDPBandwidthMeasurementReceiver.getIperf3Protocol());
            assertTrue(iperfUDPBandwidthMeasurementReceiver.getBitrate() > 0);
            assertEquals("Mbits/s", iperfUDPBandwidthMeasurementReceiver.getBandwidthUnit());
            assertEquals(IperfRole.RECEIVER, iperfUDPBandwidthMeasurementReceiver.getIperfRole());

            assertTrue(iperfUDPBandwidthMeasurementReceiver.getJitter() >= 0.00f);
            assertTrue(iperfUDPBandwidthMeasurementReceiver.getLostDatagrams() >= 0);
            assertTrue(iperfUDPBandwidthMeasurementReceiver.getTotalDatagrams() > 0);
    }

    private String createNewTestLink(String nodeUuid) throws Exception {
        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(pnaUuid, nodeUuid);
        String linkAsJson = objectMapper.writeValueAsString(createNewLinkDTO);
        MvcResult linkResult = this.mockMvc.perform(post("/api/v1/links")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(linkAsJson))
                        .andExpect(status().isCreated())
                        .andReturn();
        return objectMapper.readTree(linkResult.getResponse().getContentAsString()).get("linkUUID").asText();
    }

    private String createNewTestDestNode() throws Exception {
        CreateNewNodeDTO createNewNodeDTO = NodeDTOUtil.createTestDestNode(localAddress);
        String nodeAsJson = objectMapper.writeValueAsString(createNewNodeDTO);
        MvcResult nodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                .contentType("application/json")
                .accept("application/json")
                .content(nodeAsJson))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("nodeUUID").asText();
    }


}
