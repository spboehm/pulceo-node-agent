package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.application.ApplicationDTO;
import dev.pulceo.pna.dto.application.CreateNewApplicationDTO;
import dev.pulceo.pna.dto.metricrequests.CreateNewResourceUtilizationDTO;
import dev.pulceo.pna.dto.metricrequests.ShortNodeMetricResponseDTO;
import dev.pulceo.pna.exception.ApplicationServiceException;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.resources.K8sResourceType;
import dev.pulceo.pna.model.resources.ResourceUtilizationRequest;
import dev.pulceo.pna.model.resources.ResourceUtilizationType;
import dev.pulceo.pna.service.ApplicationService;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.NodeService;
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
    private final JobService jobService;
    private final NodeService nodeService;

    @Autowired
    public ApplicationController(ApplicationService applicationService, JobService jobService, NodeService nodeService) {
        this.applicationService = applicationService;
        this.jobService = jobService;
        this.nodeService = nodeService;
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

    @PostMapping("/{uuid}/metric-requests")
    // TODO: application node metric response dtp
    public ResponseEntity<ShortNodeMetricResponseDTO> createNewMetricRequest(@Valid @PathVariable UUID uuid, @Valid @RequestBody CreateNewResourceUtilizationDTO createNewResourceUtilizationDTO) throws ApplicationServiceException, JobServiceException {
        Optional<Application> application = this.applicationService.findApplicationByUUID(uuid);
        if (application.isEmpty()) {
            throw new ApplicationServiceException(String.format("Application with UUID %s not found", uuid));
        }

        // Create Request
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.getTypeFromString(createNewResourceUtilizationDTO.getType()))
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(application.get().getName())
                .build();
        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.getTypeFromString(createNewResourceUtilizationDTO.getType()))
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(createNewResourceUtilizationDTO.getRecurrence())
                .build();
        ResourceUtilizationJob savedResourceUtilizationJob = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob);
        Optional<Node> retrievedNode = this.nodeService.readLocalNode();
        Node fullNode = this.nodeService.readNodeByUUID(retrievedNode.get().getUuid()).get();
        fullNode.addJob(resourceUtilizationJob);

        // if enabled
        if (createNewResourceUtilizationDTO.isEnabled()) {
            this.jobService.enableResourceUtilizationJob(resourceUtilizationJob.getId());
        }

        long id = this.jobService.scheduleResourceUtilizationJob(savedResourceUtilizationJob.getId());
        Optional<ResourceUtilizationJob> createdResourceUtilizationJob = this.jobService.readNodeResourceUtilizationJob(id);
        if (createdResourceUtilizationJob.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ShortNodeMetricResponseDTO createdShortNodeMetricResponseDTO = new ShortNodeMetricResponseDTO(createdResourceUtilizationJob.get().getUuid(), fullNode.getUuid(), ResourceUtilizationType.getName(createdResourceUtilizationJob.get().getResourceUtilizationType()), String.valueOf(createdResourceUtilizationJob.get().getRecurrence()), createdResourceUtilizationJob.get().isEnabled());
        return new ResponseEntity<>(createdShortNodeMetricResponseDTO, HttpStatus.CREATED);
    }

    // TODO: add exception handler

}
