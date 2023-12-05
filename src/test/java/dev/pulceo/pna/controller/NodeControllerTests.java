package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dtos.NodeDTOUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class NodeControllerTests {

    @Autowired
    private MockMvc mockMvc;

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
        this.mockMvc.perform(post("/api/v1/nodes")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"));

        this.mockMvc.perform(get("/api/v1/nodes/" + testDestNode.getPnaUUID())
                        .contentType("application/json")
                        .accept("application/json")
                        .content(testDestNodeAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnaUUID").value("4c961268-df2a-49c1-965a-2e5036158ac0"));
    }

}
