package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import dev.pulceo.pna.repository.NodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NodeControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NodeRepository nodeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateNode() throws Exception {
        // given
        CreateNewNodeDTO testDestNode = NodeDTOUtil.createTestDestNode();
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
        CreateNewNodeDTO testDestNode = NodeDTOUtil.createTestDestNode();
        String testDestNodeAsJson = objectMapper.writeValueAsString(testDestNode);

        // when
        MvcResult createNewNodeResult = this.mockMvc.perform(post("/api/v1/nodes")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"))
                .andReturn();
        System.out.println(createNewNodeResult.getResponse().getContentAsString());
        String nodeUuid = objectMapper.readTree(createNewNodeResult.getResponse().getContentAsString()).get("nodeUUID").asText();

        MvcResult readNodeResult = this.mockMvc.perform(get("/api/v1/nodes/" + nodeUuid)
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"))
                .andReturn();
    }

}
