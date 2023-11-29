package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.CloudRegistrationRequestDto;
import dev.pulceo.pna.dto.CloudRegistrationResponseDto;
import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.service.CloudRegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping
    public ResponseEntity<CloudRegistrationResponseDto> newCloudRegistration(@Valid @RequestBody CloudRegistrationRequestDto cloudRegistrationRequestDto) {
        // TODO: validate
        System.out.println(cloudRegistrationRequestDto);
        CloudRegistration cloudRegistration = this.modelMapper.map(cloudRegistrationRequestDto, CloudRegistration.class);
        System.out.println(cloudRegistration);

        // TODO: service call


        // TODO: return CloudRegistrationResponseDto
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
