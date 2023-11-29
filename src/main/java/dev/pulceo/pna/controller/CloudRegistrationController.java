package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.CloudRegistrationRequestDto;
import dev.pulceo.pna.dto.CloudRegistrationResponseDto;
import dev.pulceo.pna.exception.CloudRegistrationException;
import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
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
    public ResponseEntity<Object> newInitialCloudRegistration(@Valid @RequestBody CloudRegistrationRequestDto cloudRegistrationRequestDto) {
        try {
            // TODO: fix here
            CloudRegistrationRequest cloudRegistrationRequest = this.modelMapper.map(cloudRegistrationRequestDto, CloudRegistrationRequest.class);
            CloudRegistration cloudRegistration = this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
            System.out.println("was here");
            return new ResponseEntity<>(this.modelMapper.map(cloudRegistration, CloudRegistrationResponseDto.class), HttpStatus.OK);
        } catch (CloudRegistrationException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

}
