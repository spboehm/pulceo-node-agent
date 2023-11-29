package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.CloudRegistrationRequestDto;
import dev.pulceo.pna.model.registration.CloudRegistration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.modelmapper.record.RecordModule;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CloudRequestRegistrationDtosUnitTest {

    private final static ModelMapper modelMapper = new ModelMapper();

    @BeforeAll
    static void setup() {
        modelMapper.registerModule(new RecordModule());
    }

    @Test
    public void testMapCloudRegistrationRequestDto() {
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String token = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, token);
        CloudRegistration cloudRegistration = modelMapper.map(cloudRegistrationRequestDto, CloudRegistration.class);

        assertEquals(cloudRegistrationRequestDto.prmUUID(), cloudRegistration.getPrmUUID());
        assertEquals(cloudRegistrationRequestDto.prmEndpoint(), cloudRegistration.getPrmEndpoint());
        assertEquals(cloudRegistrationRequestDto.pnaInitToken(), cloudRegistration.getPnaToken());
    }


}
