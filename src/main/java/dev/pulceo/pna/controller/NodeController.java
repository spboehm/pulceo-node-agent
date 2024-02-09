package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dto.node.NodeDTO;
import dev.pulceo.pna.dto.node.cpu.CPUResourceDTO;
import dev.pulceo.pna.model.node.CPU;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.service.NodeService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public NodeController(NodeService nodeService, ModelMapper modelMapper) {
        this.nodeService = nodeService;
        this.modelMapper = modelMapper;
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
            // TODO: remove modelMapper and use builder instead
            return new ResponseEntity<>(this.modelMapper.map(node.get().getCpuResource(), CPUResourceDTO.class), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // TODO: add handler
    @PutMapping("/{uuid}/cpu")
    public ResponseEntity<String> updateCPU(@PathVariable String uuid, @RequestBody CPU cpu) {
       return ResponseEntity.ok().body("");
    }

    // TODO: add exceptionHandler
}
