package dev.pulceo.pna.service;

import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NodeService {

    @Autowired
    NodeRepository nodeRepository;

    public Node createNode(Node node) {
        // TODO: add further validation, throw exception in case a problem arises
        return this.nodeRepository.save(node);
    }

    public Optional<Node> readNode(long id) {
        return this.nodeRepository.findById(id);
    }



}
