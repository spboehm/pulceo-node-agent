package dev.pulceo.pna.service;

import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.NodeRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;

    @Autowired
    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Value("${pna.id}")
    private String pnaId;

    @Value("${pna.node.name}")
    private String nodeName;

    @Value("${pna.node.endpoint}")
    private String nodeEndpoint;

    public Node createNode(Node node) {
        // TODO: add further validation, throw exception in case a problem arises
        return this.nodeRepository.save(node);
    }

    public Optional<Node> readNode(long id) {
        return this.nodeRepository.findById(id);
    }

    public Optional<Node> readLocalNode() {
        return this.nodeRepository.findByIsLocalNode(true);
    }

    @PostConstruct
    public void initLocalNode() {

        // check if local node already exists
        Optional<Node> localNode = this.nodeRepository.findByPnaId(pnaId);

        if (localNode.isPresent()) {
            return;
        }

        this.createNode(Node.builder()
                .pnaId(pnaId)
                .isLocalNode(true)
                .name(nodeName)
                .endpoint(nodeEndpoint)
                .build());
    }

}
