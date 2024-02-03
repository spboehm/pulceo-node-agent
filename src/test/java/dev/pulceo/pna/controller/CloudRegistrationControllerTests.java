package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.registration.CloudRegistrationRequestDto;
import dev.pulceo.pna.repository.CloudRegistrationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=7002", "pna.delay.udp.port=7003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440004"})
@AutoConfigureMockMvc
public class CloudRegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CloudRegistrationRepository cloudRegistrationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    String uuidRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    String base64EncodedRegex = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";

    @BeforeEach
    public void setUp() {
        cloudRegistrationRepository.deleteAll();
    }

    // TODO: Add similar test cases if one of the attributes is missing
    @Test
    public void testNewCloudRegistrationWithSuccess() throws Exception {
        // given
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String token = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, token);
        String cloudRegistrationRequestDtoAsJson = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                .contentType("application/json")
                .accept("application/json")
                .content(cloudRegistrationRequestDtoAsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodeUUID", matchesPattern(uuidRegex)))
                .andExpect(jsonPath("$.pnaUUID", matchesPattern(uuidRegex)))
                .andExpect(jsonPath("$.prmUUID").value(prmUUID))
                .andExpect(jsonPath("$.prmEndpoint").value(prmEndpoint))
                .andExpect(jsonPath("$.pnaToken", matchesPattern(base64EncodedRegex)));
    }

    @Test
    public void testNewCloudRegistrationWithWrongPrmUUID() throws Exception {
        // given
        String prmUUID = "prm-3asssadasdasdsbd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, pnaInitToken);
        String json = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }

    @Test
    public void testNewCloudRegistrationWithWrongPrmEndpoint() throws Exception {
        // given
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "httpss://localhasdadasost:7878878997";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, pnaInitToken);
        String json = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewCloudRegistrationWithWrongPnaInitToken() throws Exception {
        // given
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAHUHM3MGtRRWNsZVFMSmdZclFhVUExb0Vp====";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, pnaInitToken);
        String json = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void testNewCloudRegistrationWhenRegistrationAlreadyExists() throws Exception {
        // given
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, pnaInitToken);
        String json = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                        .andExpect(status().isOk());

        MvcResult mvcResult = this.mockMvc.perform(post("/api/v1/cloud-registrations")
                        .contentType("application/json")
                        .accept("application/json")
                        .content(json))
                        .andExpect(status().isBadRequest())
                        .andReturn();
    }


}
