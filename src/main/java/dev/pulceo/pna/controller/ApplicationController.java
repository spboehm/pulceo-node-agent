package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.application.ApplicationComponentDTO;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("")
    public ResponseEntity<List<ApplicationDTO>> readAllApplications() {
        return null;
    }

    @PostMapping("")
    public ResponseEntity<ApplicationDTO> createNewApplication(@Valid @RequestBody CreateNewApplicationDTO createNewApplicationDTO) throws ApplicationServiceException {
        Application application = this.applicationService.createApplication(Application.fromCreateNewApplicationDTO(createNewApplicationDTO));
        return new ResponseEntity<>(ApplicationDTO.fromApplication(application), HttpStatus.CREATED);
    }

    @PostMapping("/{uuid}/application-components")
    public ResponseEntity<ApplicationComponentDTO> createNewApplicationComponent(@PathVariable String uuid, @Valid @RequestBody ApplicationComponentDTO applicationComponentDTO) {




        return null;
    }

    @DeleteMapping("/{uuid}")
    public void deleteApplication(@PathVariable String uuid) {

    }

    // TODO: add exception handler

}
