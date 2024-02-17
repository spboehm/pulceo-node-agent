package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.metricrequests.CreateNewResourceUtilizationDTO;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.resources.CPUUtilizationMeasurement;
import dev.pulceo.pna.repository.NodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=9002", "pna.delay.udp.port=9003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440007"})
@AutoConfigureMockMvc(addFilters = false)
public class NodeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    PublishSubscribeChannel resourceUtilizationCPUServiceMessageChannel;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pna.local.address}")
    private String localAddress;

    @Test
    public void testCreateNode() throws Exception {
        // given
        CreateNewNodeDTO testDestNode = NodeDTOUtil.createTestDestNode(localAddress);
        String testDestNodeAsJson = objectMapper.writeValueAsString(testDestNode);

        // when and then
        this.mockMvc.perform(post("/api/v1/nodes")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"));
        // TODO: add proper verification of the return entity
    }

    @Test
    public void testReadNodeByUUID() throws Exception {
        // given
        CreateNewNodeDTO testDestNode = NodeDTOUtil.createTestDestNode(localAddress);
        String testDestNodeAsJson = objectMapper.writeValueAsString(testDestNode);

        // when
        MvcResult createNewNodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"))
                .andReturn();
        String nodeUuid = objectMapper.readTree(createNewNodeResult.getResponse().getContentAsString()).get("nodeUUID").asText();

        MvcResult readNodeResult = this.mockMvc.perform(get("/api/v1/nodes/" + nodeUuid)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"))
                .andReturn();
    }

    @Test
    public void testNewCPUMetricRequest() throws Exception {
        // given

        // when
        CreateNewResourceUtilizationDTO createNewResourceUtilizationDTO = new CreateNewResourceUtilizationDTO("cpu-util", 15, true);
        String createNewResourceUtilizationDTOAsJson = objectMapper.writeValueAsString(createNewResourceUtilizationDTO);
        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/nodes/localNode/metric-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(createNewResourceUtilizationDTOAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.remoteMetricRequestUUID").isNotEmpty())
                .andExpect(jsonPath("$.remoteNodeUUID").isNotEmpty())
                .andExpect(jsonPath("$.type").value("cpu-util"))
                .andExpect(jsonPath("$.recurrence").value("15"))
                .andExpect(jsonPath("$.enabled").value("true"))
                .andReturn();
        String metricRequestUuid = objectMapper.readTree(mvcResult.getResponse().getContentAsString()).get("remoteMetricRequestUUID").asText();
        // TODO: cancel

        BlockingQueue<Message> cpuUtilizationResult = new ArrayBlockingQueue<>(10);
        this.resourceUtilizationCPUServiceMessageChannel.subscribe(message -> cpuUtilizationResult.add((Message) message.getPayload()));
        // initiate orderly shutdown
        Message message = cpuUtilizationResult.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        CPUUtilizationMeasurement cpuUtilizationMeasurement = (CPUUtilizationMeasurement) map.get("cpuUtilizationMeasurement");

        // then
        assertNotNull(map);
        assert("127.0.0.1".equals(map.get("sourceHost")));
        assertTrue(cpuUtilizationMeasurement.getUsageCPUPercentage() >= 0.0f);
        assertTrue(cpuUtilizationMeasurement.getUsageNanoCores() >= 0.0f);
        assertTrue(cpuUtilizationMeasurement.getUsageCoreNanoSeconds() >= 0.0f);
    }

}
