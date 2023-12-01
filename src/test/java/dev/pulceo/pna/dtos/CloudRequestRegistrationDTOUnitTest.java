package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.registration.CloudRegistrationRequestDto;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CloudRequestRegistrationDTOUnitTest {

    private final ModelMapper modelMapper = new ModelMapper();

    @Test
    public void testMapCloudRegistrationRequestDto() {
        String prmUUID = "3768f6c8-dd4e-4c12-b76b-54bd0e1cf5fa";
        String prmEndpoint = "http://localhost:7878";
        String token = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        CloudRegistrationRequestDto cloudRegistrationRequestDto = new CloudRegistrationRequestDto(prmUUID, prmEndpoint, token);
        CloudRegistrationRequest cloudRegistrationRequest = modelMapper.map(cloudRegistrationRequestDto, CloudRegistrationRequest.class);

        assertEquals(cloudRegistrationRequestDto.getPrmUUID(), cloudRegistrationRequest.getPrmUUID());
        assertEquals(cloudRegistrationRequestDto.getPrmEndpoint(), cloudRegistrationRequest.getPrmEndpoint());
        assertEquals(cloudRegistrationRequestDto.getPnaInitToken(), cloudRegistrationRequest.getPnaInitToken());
    }


}
