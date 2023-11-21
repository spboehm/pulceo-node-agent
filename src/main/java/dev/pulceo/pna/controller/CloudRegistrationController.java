package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.CloudRegistrationRequestDto;
import dev.pulceo.pna.dto.CloudRegistrationResponseDto;
import dev.pulceo.pna.service.CloudRegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cloud-registrations")
public class CloudRegistrationController {

    private final CloudRegistrationService cloudRegistrationService;

    private final ModelMapper modelMapper;

    @Autowired
    public CloudRegistrationController(CloudRegistrationService cloudRegistrationService, ModelMapper modelMapper) {
        this.cloudRegistrationService = cloudRegistrationService;
        this.modelMapper = modelMapper;
    }


    public CloudRegistrationResponseDto newCloudRegistration(@RequestBody @Valid CloudRegistrationRequestDto cloudRegistrationRequestDto) {
        // TODO: Validate CloudRegistrationRequestDto
        // TODO: invoke service cloudRegistrationService
        // TODO: return CloudRegistrationResponseDto
        return new CloudRegistrationResponseDto();
    }

}
