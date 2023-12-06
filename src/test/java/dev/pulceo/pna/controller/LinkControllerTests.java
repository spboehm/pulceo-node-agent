package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestDTO;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.LinkDTOUtil;
import dev.pulceo.pna.dtos.MetricRequestDTOUtil;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import dev.pulceo.pna.repository.LinkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LinkRepository linkRepository;

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
        String nodeUuid = objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("pnaUUID").asText();

        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(pnaUuid, nodeUuid);
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
        String nodeUuid = objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("pnaUUID").asText();

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
        CreateNewMetricRequestDTO createNewMetricRequestDTO = MetricRequestDTOUtil.createTestMetricRequest("icmp-rtt");
        String metricRequestAsJson = objectMapper.writeValueAsString(createNewMetricRequestDTO);
        MvcResult metricRequestResult = this.mockMvc.perform(post("/api/v1/links/" + linkUuid + "/metric-requests")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(metricRequestAsJson))
                        .andExpect(status().isOk())
                        .andReturn();
        System.out.println(metricRequestResult.getResponse().getContentAsString());
    }


}
