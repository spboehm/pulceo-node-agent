package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.application.ApplicationDTO;
import dev.pulceo.pna.dto.application.CreateNewApplicationDTO;
import dev.pulceo.pna.exception.ApplicationServiceException;
import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<ApplicationDTO> readApplicationByUUID(@Valid @PathVariable UUID uuid) throws ApplicationServiceException {
        Optional<Application> application = this.applicationService.findApplicationByUUID(uuid);
        if (application.isEmpty()) {
            throw new ApplicationServiceException(String.format("Application with UUID %s not found", uuid));
        }
        return new ResponseEntity<>(ApplicationDTO.fromApplication(application.get()), HttpStatus.OK);
    }

    @PostMapping("")
    public ResponseEntity<ApplicationDTO> createNewApplication(@Valid @RequestBody CreateNewApplicationDTO createNewApplicationDTO) throws ApplicationServiceException {
        Application application = this.applicationService.createApplication(Application.fromCreateNewApplicationDTO(createNewApplicationDTO));
        return new ResponseEntity<>(ApplicationDTO.fromApplication(application), HttpStatus.CREATED);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable UUID uuid) throws ApplicationServiceException {
        Optional<Application> application = this.applicationService.findApplicationByUUID(uuid);
        if (application.isEmpty()) {
            throw new ApplicationServiceException(String.format("Application with UUID %s not found", uuid));
        }
        this.applicationService.deleteApplication(application.get().getName());
    }

    // TODO: add exception handler

}
