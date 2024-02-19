package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.application.ApplicationDTO;
import dev.pulceo.pna.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<ApplicationDTO> createNewApplication() {

        // TODO:
        // Application: name
        // List of Application components
        // application component: name, image, port, protocol, applicationComponentType
        // the application
        // and the node which is the local node

        return null;
    }

    @DeleteMapping("/{uuid}")
    public void deleteApplication(@PathVariable String uuid) {

    }

}
