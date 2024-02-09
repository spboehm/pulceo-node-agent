package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.NodeServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.CPU;
import dev.pulceo.pna.model.node.CPUResource;
import dev.pulceo.pna.model.node.Memory;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.NodeRepository;
import dev.pulceo.pna.util.CPUUtil;
import dev.pulceo.pna.util.ProcessUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class NodeService {

    private final NodeRepository nodeRepository;

    @Autowired
    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Value("${pna.uuid}")
    private String pnaUUID;

    @Value("${pna.node.endpoint}")
    private String nodeEndpoint;

    @Value("${pna.host}")
    private String host;

    public Node createNode(Node node) {
        // TODO: add further validation, throw exception in case a problem arises
        return this.nodeRepository.save(node);
    }

    public Optional<Node> readNode(long id) {
        return this.nodeRepository.findById(id);
    }

    public Optional<Node> readNodeByPnaUUID(String uuid) {
        return this.nodeRepository.findByPnaUUID(uuid);
    }

    public Optional<Node> readNodeByUUID(UUID uuid) {
        return this.nodeRepository.findByUuid(uuid);
    }

    public Optional<Node> readLocalNode() {
        return this.nodeRepository.findByIsLocalNode(true);
    }

    public CPU obtainCPUInformation() throws NodeServiceException {
        try {
            Process lscpuProcess = new ProcessBuilder("lscpu").start();
            lscpuProcess.waitFor();
            return CPUUtil.extractCPUInformation(ProcessUtils.readProcessOutput(lscpuProcess.getInputStream()));
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new NodeServiceException("Could not obtain CPU information", e);
        }
    }

    public Memory obtainMemoryInformation() throws NodeServiceException {
        return null;
    }

    public Optional<CPUResource> readLocalCPUResource() {
        Optional<Node> localNode = this.readLocalNode();
        if (localNode.isPresent()) {
            return Optional.of(localNode.get().getCpuResource());
        } else {
            return Optional.empty();
        }
    }

    @PostConstruct
    private void initLocalNode() throws NodeServiceException {
        // check if local node already exists
        Optional<Node> localNode = this.readLocalNode();

        if (localNode.isPresent()) {
            return;
        }

        // obtain cpu information
        CPU cpuAllocatable = this.obtainCPUInformation();
        CPU cpuCapacity = this.obtainCPUInformation();


        this.createNode(Node.builder()
                .pnaUUID(pnaUUID)
                .isLocalNode(true)
                .name(host)
                .pnaEndpoint(nodeEndpoint)
                .host(host)
                .cpuResource(CPUResource.builder().cpuAllocatable(cpuAllocatable).cpuCapacity(cpuCapacity).build())
                .build());
    }

}
