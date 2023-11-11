package dev.pulceo.pna.controller;

import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.service.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("api/v1/nodes/")
public class NodeController {

    @Autowired
    NodeService nodeService;

    @GetMapping("{id}")
    public ResponseEntity<Node> getNodeById(@PathVariable long id) {
        Optional<Node> node = this.nodeService.readNode(id);
        if (node.isPresent()) {
            return new ResponseEntity<>(node.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
