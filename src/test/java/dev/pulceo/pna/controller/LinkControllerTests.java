package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestDTO;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.LinkDTOUtil;
import dev.pulceo.pna.dtos.MetricRequestDTOUtil;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.repository.LinkRepository;
import dev.pulceo.pna.repository.NodeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=8002", "pna.delay.udp.port=8003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440005"})
@AutoConfigureMockMvc
public class LinkControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;
    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    PublishSubscribeChannel pingServiceMessageChannel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pna.uuid}")
    private String pnaUuid;

    @BeforeEach
    public void setUp() {
        linkRepository.deleteAll();
    }

    @Test
    public void testCreateLink() throws Exception {
        // given
        CreateNewNodeDTO createNewNodeDTO = NodeDTOUtil.createTestDestNode();
        String nodeAsJson = objectMapper.writeValueAsString(createNewNodeDTO);
        MvcResult nodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(nodeAsJson))
                .andExpect(status().isCreated())
                .andReturn();
        String nodeUuid = objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("nodeUUID").asText();
        Optional<Node> localNode = this.nodeRepository.findByPnaUUID(pnaUuid);
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
    public void testNewICMPRTTRequest() throws Exception {
        // given
        // new node
        CreateNewNodeDTO createNewNodeDTO = NodeDTOUtil.createTestDestNode();
        String nodeAsJson = objectMapper.writeValueAsString(createNewNodeDTO);
        MvcResult nodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                .contentType("application/json")
                .accept("application/json")
                .content(nodeAsJson))
                .andExpect(status().isCreated())
                .andReturn();
        String nodeUuid = objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("nodeUUID").asText();

        // link between local node and new node
        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(pnaUuid, nodeUuid);
        String linkAsJson = objectMapper.writeValueAsString(createNewLinkDTO);
        MvcResult linkResult = this.mockMvc.perform(post("/api/v1/links")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(linkAsJson))
                        .andExpect(status().isCreated())
                        .andReturn();
        String linkUuid = objectMapper.readTree(linkResult.getResponse().getContentAsString()).get("linkUUID").asText();

        // when and then
        CreateNewMetricRequestDTO createNewMetricRequestDTO = MetricRequestDTOUtil.createIcmpRttMetricRequestDTO("icmp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestDTO);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests/icmp-rtt-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                        .andExpect(status().isOk())
                        .andReturn();

        // wait for icmp-rtt value
        BlockingQueue<Message> messageBlockingQueue = new ArrayBlockingQueue<>(1);
        this.pingServiceMessageChannel.subscribe(message -> messageBlockingQueue.add((Message) message.getPayload()));
        Message message = messageBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        PingDelayMeasurement pingDelayMeasurement = (PingDelayMeasurement) map.get("pingDelayMeasurement");

        // then
        // TODO: refactor and expand the test to other metric values
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
