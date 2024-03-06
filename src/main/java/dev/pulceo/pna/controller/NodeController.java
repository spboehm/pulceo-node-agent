package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.metricrequests.CreateNewResourceUtilizationDTO;
import dev.pulceo.pna.dto.metricrequests.ShortNodeMetricResponseDTO;
import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dto.node.NodeDTO;
import dev.pulceo.pna.dto.node.cpu.CPUResourceDTO;
import dev.pulceo.pna.dto.node.memory.MemoryResourceDTO;
import dev.pulceo.pna.dto.node.storage.StorageResourceDTO;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.NodeServiceException;
import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import dev.pulceo.pna.model.node.CPU;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.resources.K8sResourceType;
import dev.pulceo.pna.model.resources.ResourceUtilizationRequest;
import dev.pulceo.pna.model.resources.ResourceUtilizationType;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.NodeService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final ModelMapper modelMapper;
    private final JobService jobService;

    @Value("${k3s.nodename}")
    private String k3sNodeName;

    @Autowired
    public NodeController(NodeService nodeService, ModelMapper modelMapper, JobService jobService) {
        this.nodeService = nodeService;
        this.modelMapper = modelMapper;
        this.jobService = jobService;
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<NodeDTO> getNodeByUUID(@PathVariable String uuid) {
        Optional<Node> node = this.nodeService.readNodeByUUID(UUID.fromString(uuid));
        if (node.isPresent()) {
            return new ResponseEntity<>(this.modelMapper.map(node.get(), NodeDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("")
    public ResponseEntity<NodeDTO> createNode(@Valid @RequestBody CreateNewNodeDTO createNewNodeDTO) {
        Node node = this.modelMapper.map(createNewNodeDTO, Node.class);
        Node createdNode = this.nodeService.createNode(node);
        return new ResponseEntity<>(this.modelMapper.map(createdNode, NodeDTO.class), HttpStatus.CREATED);
    }

    @GetMapping("/localNode/cpu")
    public ResponseEntity<CPUResourceDTO> readCPUResources() {
        Optional<Node> node = this.nodeService.readLocalNode();
        if (node.isPresent()) {
            // TODO: remove modelMapper and use builder instead, values are missing...
            return new ResponseEntity<>(this.modelMapper.map(node.get().getCpuResource(), CPUResourceDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/localNode/memory")
    public ResponseEntity<MemoryResourceDTO> readMemoryResources() {
        Optional<Node> node = this.nodeService.readLocalNode();
        if (node.isPresent()) {
            return new ResponseEntity<>(this.modelMapper.map(node.get().getMemoryResource(), MemoryResourceDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/localNode/storage")
    public ResponseEntity<StorageResourceDTO> readStorageResources() {
        Optional<Node> node = this.nodeService.readLocalNode();
        if (node.isPresent()) {
            return new ResponseEntity<>(this.modelMapper.map(node.get().getStorageResource(), StorageResourceDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/localNode/metric-requests")
    public ResponseEntity<ShortNodeMetricResponseDTO> createMetricRequest(@Valid @RequestBody CreateNewResourceUtilizationDTO createNewResourceUtilizationDTO) throws JobServiceException {
        Optional<Node> retrievedNode = this.nodeService.readLocalNode();

        if (retrievedNode.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Create Request
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.getTypeFromString(createNewResourceUtilizationDTO.getType()))
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(this.k3sNodeName)
                .build();
        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.getTypeFromString(createNewResourceUtilizationDTO.getType()))
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(createNewResourceUtilizationDTO.getRecurrence())
                .build();
        ResourceUtilizationJob savedResourceUtilizationJob = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob);
        Node fullNode = this.nodeService.readNodeByUUID(retrievedNode.get().getUuid()).get();
        fullNode.addJob(resourceUtilizationJob);
        this.nodeService.updateNode(fullNode);

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

    @DeleteMapping("/localNode/metric-requests/{metricRequestUUID}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteMetricRequest(@PathVariable UUID metricRequestUUID) throws NodeServiceException {
        Optional<ResourceUtilizationJob> resourceUtilizationJob = this.jobService.readNodeResourceUtilizationJobByUUID(metricRequestUUID);
        if (resourceUtilizationJob.isEmpty()) {
           throw new NodeServiceException("ResourceUtilizationJob not found");
        }
        this.jobService.deleteJobByUUID(metricRequestUUID);
    }


    // TODO: add handler
    @PutMapping("/{uuid}/cpu")
    public ResponseEntity<String> updateCPU(@PathVariable String uuid, @RequestBody CPU cpu) {
       return ResponseEntity.ok().body("");
    }

    // TODO: add exceptionHandler
}
