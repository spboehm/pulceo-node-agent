package dev.pulceo.pna.controller;

import dev.pulceo.pna.dto.node.CreateNewNodeDTO;
import dev.pulceo.pna.dto.node.NodeDTO;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.service.NodeService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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

    // TODO: get by UUID
    @GetMapping("/{id}")
    public ResponseEntity<Node> getNodeById(@PathVariable long id) {
        Optional<Node> node = this.nodeService.readNode(id);
        if (node.isPresent()) {
            return new ResponseEntity<>(node.get(), HttpStatus.OK);
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


    // TODO: add exceptionHandler

}
