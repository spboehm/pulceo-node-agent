package dev.pulceo.pna.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.dto.CloudRegistrationRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CloudRegistrationControllerTests {

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    // TODO: Add similar test cases if one of the attributes is missing
    @Test
    public void testNewCloudRegistration() throws Exception {
        // given
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String token = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, token);
        String json = objectMapper.writeValueAsString(cloudRegistrationRequestDto);

        // when and then
        this.mockMvc.perform(post("/api/v1/cloud-registrations")
                .contentType("application/json")
                .accept("application/json")
                .content(json))
                .andExpect(status().isOk());
    }

}
