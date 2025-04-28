package dev.pulceo.pna.controller;

import dev.pulceo.pna.service.ManagedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orchestration-context")
public class OrchestrationContextController {

    private final List<ManagedService> managedServices;

    @Autowired
    public OrchestrationContextController(List<ManagedService> managedServices) {
        this.managedServices = managedServices;
    }

    @PostMapping("/reset")
    public void reset() {
        for (ManagedService managedService : managedServices) {
            managedService.reset();
        }
    }
}
