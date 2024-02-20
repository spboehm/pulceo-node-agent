package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.application.ApplicationDTO;
import dev.pulceo.pna.repository.ApplicationComponentRepository;
import dev.pulceo.pna.repository.ApplicationRepository;
import dev.pulceo.pna.service.KubernetesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=11002", "pna.delay.udp.port=11003", "pna.mqtt.client.id=6705b0e1-1791-449b-b76e-2a824ca270c0"})
@AutoConfigureMockMvc(addFilters = false)
public class ApplicationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KubernetesService kubernetesService;

    @Autowired
    ApplicationRepository applicationRepository;

    @Autowired
    ApplicationComponentRepository applicationComponentRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        applicationRepository.deleteAll();
        applicationComponentRepository.deleteAll();
        this.kubernetesService.deleteNamespace("pulceo");
    }

    @Test
    public void testCreateNewApplicationWithoutApplicationComponents() throws Exception {
        // given
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .name("test-application")
                .build();
        String applicationDTOAsString = objectMapper.writeValueAsString(applicationDTO);

        // when and then
        this.mockMvc.perform(post("/api/v1/applications")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(applicationDTOAsString))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("test-application"));

    }





}
