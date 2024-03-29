package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.NodeServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.*;
import dev.pulceo.pna.repository.NodeRepository;
import dev.pulceo.pna.util.CPUUtil;
import dev.pulceo.pna.util.MemoryUtil;
import dev.pulceo.pna.util.ProcessUtils;
import dev.pulceo.pna.util.StorageUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class NodeService {

    private final Logger logger = LoggerFactory.getLogger(NodeService.class);

    private final NodeRepository nodeRepository;

    @Autowired
    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Value("${pna.uuid}")
    private String pnaUUID;

    @Value("${pna.node.endpoint}")
    private String nodeEndpoint;

    @Value("${pna.host.fqdn}")
    private String host;

    public Node createNode(Node node) {
        // TODO: add further validation, throw exception in case a problem arises
        return this.nodeRepository.save(node);
    }

    public Node updateNode(Node node) {
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
        Process lscpuProcess = null;
        try {
            lscpuProcess = new ProcessBuilder("lscpu").start();
            lscpuProcess.waitFor();
            return CPUUtil.extractCPUInformation(ProcessUtils.readProcessOutput(lscpuProcess.getInputStream()));
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new NodeServiceException("Could not obtain CPU information", e);
        } finally {
            try {
                ProcessUtils.closeProcess(lscpuProcess);
            } catch (IOException e) {
                logger.error("Could not close process", e);
            }
        }
    }

    public Memory obtainMemoryInformation() throws NodeServiceException {
        Process procMemInfoAsString = null;
        try {
            procMemInfoAsString = new ProcessBuilder("cat", "/proc/meminfo").start();
            procMemInfoAsString.waitFor();
            return MemoryUtil.extractMemoryInformation(ProcessUtils.readProcessOutput(procMemInfoAsString.getInputStream()));
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new NodeServiceException("Could not obtain memory information", e);
        } finally {
            try {
                ProcessUtils.closeProcess(procMemInfoAsString);
            } catch (IOException e) {
                logger.error("Could not close process", e);
            }
        }
    }

    public Storage obtainStorageInformation() throws NodeServiceException {
        Process storageInfoAsString = null;
        try {
            storageInfoAsString = new ProcessBuilder("df", "-h", "/").start();
            storageInfoAsString.waitFor();
            return StorageUtil.extractStorageInformation(ProcessUtils.readProcessOutput(storageInfoAsString.getInputStream()));
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new NodeServiceException("Could not obtain storage information", e);
        } finally {
            try {
                ProcessUtils.closeProcess(storageInfoAsString);
            } catch (IOException e) {
                logger.error("Could not close process", e);
            }
        }
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
        Memory memoryAllocatable = this.obtainMemoryInformation();
        Memory memoryCapacity = this.obtainMemoryInformation();
        Storage storageCapacity = this.obtainStorageInformation();

        this.createNode(Node.builder()
                .pnaUUID(pnaUUID)
                .isLocalNode(true)
                .name(host)
                .pnaEndpoint(nodeEndpoint)
                .host(host)
                .cpuResource(CPUResource.builder().cpuAllocatable(cpuAllocatable).cpuCapacity(cpuCapacity).build())
                .memoryResource(MemoryResource.builder().memoryAllocatable(memoryAllocatable).memoryCapacity(memoryCapacity).build())
                .storageResource(StorageResource.builder().storageCapacity(storageCapacity).build())
                .build());
    }

}
