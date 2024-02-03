package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.registration.CloudRegistrationRequestDto;
import dev.pulceo.pna.dto.registration.CloudRegistrationResponseDto;
import dev.pulceo.pna.exception.CloudRegistrationException;
import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
import dev.pulceo.pna.service.CloudRegistrationService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/cloud-registrations")
public class CloudRegistrationController {

    private final Logger logger = LoggerFactory.getLogger(NodeController.class);

    private final CloudRegistrationService cloudRegistrationService;

    private final ModelMapper modelMapper;

    @Autowired
    public CloudRegistrationController(CloudRegistrationService cloudRegistrationService, ModelMapper modelMapper) {
        this.cloudRegistrationService = cloudRegistrationService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<CloudRegistrationResponseDto> newInitialCloudRegistration(@Valid @RequestBody CloudRegistrationRequestDto cloudRegistrationRequestDto) throws CloudRegistrationException {
        this.logger.info("Received request to create a new CloudRegistration: " + cloudRegistrationRequestDto);
        CloudRegistrationRequest cloudRegistrationRequest = this.modelMapper.map(cloudRegistrationRequestDto, CloudRegistrationRequest.class);
        CloudRegistration cloudRegistration = this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
        return new ResponseEntity<>(this.modelMapper.map(cloudRegistration, CloudRegistrationResponseDto.class), HttpStatus.OK);
    }

    @ExceptionHandler(value = CloudRegistrationException.class)
    public ResponseEntity<CustomErrorResponse> handleCloudRegistrationException(CloudRegistrationException cloudRegistrationException) {
        CustomErrorResponse error = new CustomErrorResponse("BAD_REQUEST", cloudRegistrationException.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setTimestamp(LocalDateTime.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
