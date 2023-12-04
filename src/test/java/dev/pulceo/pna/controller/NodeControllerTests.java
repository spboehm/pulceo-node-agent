package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

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
        CreateNewNodeDTO testDestNode = CreateNewNodeDTO.builder()
                .pnaUUID("551e8400-e29b-11d4-a716-446655440004")
                .name("testDestNode")
                .nodeLocationCity("Bamberg")
                .nodeLocationCountry("Germany")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
        String testDestNodeAsJson = objectMapper.writeValueAsString(testDestNode);

        // when and then
        this.mockMvc.perform(post("/api/v1/nodes")
                .contentType("application/json")
                .accept("application/json")
                .content(testDestNodeAsJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.pnaUUID").value("551e8400-e29b-11d4-a716-446655440004"));
        // TODO: add proper verification of the return entity
    }

}
