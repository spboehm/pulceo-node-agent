package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.LinkDTOUtil;
import dev.pulceo.pna.dtos.NodeDTOUtil;
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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${pna.uuid}")
    private String pnaUuid;


    @Test
    public void testCreateLink() throws Exception {
        // given
        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(pnaUuid, pnaUuid);
        String createNewLinkDTOAsJson = this.objectMapper.writeValueAsString(createNewLinkDTO);

        this.mockMvc.perform(post("/api/v1/links")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(createNewLinkDTOAsJson))
                .andExpect(status().isCreated());

    }

    @Test
    public void testNewICMPRTTRequest() throws Exception {
        // given
        // TODO: create an additional node, because the node itself does already exist
        CreateNewNodeDTO createNewNodeDTO = NodeDTOUtil.createTestDestNode();
        String nodeAsJson = objectMapper.writeValueAsString(createNewNodeDTO);
        MvcResult nodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                .contentType("application/json")
                .accept("application/json")
                .content(nodeAsJson))
                .andExpect(status().isCreated())
                .andReturn();
        String nodeUuid = objectMapper.readTree(nodeResult.getResponse().getContentAsString()).get("pnaUUID").asText();

        // TODO: create a Link between the two nodes
        CreateNewLinkDTO createNewLinkDTO = LinkDTOUtil.createTestLink(pnaUuid, nodeUuid);
        String linkAsJson = objectMapper.writeValueAsString(createNewLinkDTO);
        MvcResult linkResult = this.mockMvc.perform(post("/api/v1/links")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(linkAsJson))
                        .andExpect(status().isCreated())
                        .andReturn();
        System.out.println(linkResult.getResponse().getContentAsString());


        // TOOD: create a MetricRequestDTO for IMCP-RTT

        // when

        // then
    }


}
