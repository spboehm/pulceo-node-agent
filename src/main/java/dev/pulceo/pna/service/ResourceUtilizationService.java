package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.exception.ResourceServiceUtilizationException;
import dev.pulceo.pna.model.resources.*;
import dev.pulceo.pna.util.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ResourceUtilizationService {

    Logger logger = LoggerFactory.getLogger(ResourceUtilizationService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final NodeService nodeService;
    // TODO: fix potentially happening NPE
    private final int LOGICAL_CPU_CORES;
    private final float MEMORY_CAPACITY;

    @Value("${k3s.api.server.host}")
    private String API_SERVER_HOST;
    @Value("${k3s.api.server.port}")
    private String API_SERVER_PORT;
    @Value("${k3s.api.service.account.token.path}")
    private String API_SERVICE_ACCOUNT_TOKEN_PATH;
    @Value("${k3s.api.service.account.ca.crt.path}")
    private String API_SERVICE_ACOUNT_CA_CERT_PATH;
    @Value("${k3s.nodename}")
    private String K3S_NODENAME;

    private final Object lock = new Object();

    @Autowired
    public ResourceUtilizationService(NodeService nodeService) {
        this.nodeService = nodeService;
        LOGICAL_CPU_CORES = this.nodeService.readLocalNode().get().getCpuResource().getCpuCapacity().getThreads();
        MEMORY_CAPACITY = this.nodeService.readLocalNode().get().getMemoryResource().getMemoryCapacity().getSize();
    }

    public JsonNode readStatSummaryFromKubelet() throws ResourceServiceUtilizationException {
        int retries = 0;
        Process process = null;
        while (retries < 3) {
            try {
                String token = Files.readString(Path.of(API_SERVICE_ACCOUNT_TOKEN_PATH));
                String command = "curl --cacert " + API_SERVICE_ACOUNT_CA_CERT_PATH + " --header \"Authorization: Bearer " + token + "\" -X GET https://" + API_SERVER_HOST + ":" + API_SERVER_PORT + "/api/v1/nodes/" + K3S_NODENAME + "/proxy/stats/summary";
                synchronized (lock) {
                    String uuid = UUID.randomUUID().toString();
                    List<String> lines = List.of("#!/bin/sh", command);
                    File file = new File(uuid + ".sh");
                    Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
                    boolean executable = file.setExecutable(true);
                    if (!executable) {
                        logger.error("Could not set file to executable");
                        throw new ResourceServiceUtilizationException("Could not set file to executable");
                    }
                    ProcessBuilder processBuilder = new ProcessBuilder("sh", uuid + ".sh");
                    process = processBuilder.start();
                    process.waitFor(3, TimeUnit.SECONDS);
                    if (process.exitValue() != 0) {
                        logger.error("Could not execute process");
                        process.destroyForcibly();
                        throw new ProcessException("Could not execute process!");
                    }
                    List<String> strings = ProcessUtils.readProcessOutput(process.getInputStream());
                    boolean fileDeleted = file.delete();
                    if (!fileDeleted) {
                        logger.error("Could not delete file");
                        throw new ResourceServiceUtilizationException("Could not delete file");
                    }
                    return this.objectMapper.readTree(strings.stream().collect(Collectors.joining("\n")));
                }
            } catch (IOException | ProcessException e) {
                retries++;
                logger.error("Could not read measurement from kubelet...retry " + retries, e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    logger.error("Interrupted while waiting for retrying", e);
                    throw new ResourceServiceUtilizationException(e);
                }
            } catch (InterruptedException e) {
                logger.info("Interrupted while waiting for process to finish", e);
                throw new ResourceServiceUtilizationException(e);
            } finally {
                try {
                    ProcessUtils.closeProcess(process);
                } catch (IOException e) {
                    logger.error("Could not close process", e);
                }
            }
        }
        throw new ResourceServiceUtilizationException("Could not read measurement from kubelet...retry");
    }

    public CPUUtilizationResult retrieveCPUUtilization(ResourceUtilizationRequest resourceUtilizationRequest) throws ResourceServiceUtilizationException {
        try {
            logger.debug("Retrieving CPU utilization for: " + resourceUtilizationRequest.getResourceName());
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveCPUUtilizationForNode(jsonNode);
            } else {
                // rewrite for pulceo node components
                if (resourceUtilizationRequest.getResourceName().endsWith("-pulceo-node-agent")) {
                    CPUUtilizationResult cpuUtilizationResult = retrieveCPUUtilizationForPod(jsonNode, "pulceo-node-agent");
                    cpuUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return cpuUtilizationResult;
                } else if (resourceUtilizationRequest.getResourceName().endsWith("-traefik")) {
                    CPUUtilizationResult cpuUtilizationResult = retrieveCPUUtilizationForPod(jsonNode, "traefik");
                    cpuUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return cpuUtilizationResult;
                } else {
                    return retrieveCPUUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
                }
            }
        } catch (ResourceServiceUtilizationException e) {
            logger.error("Could not retrieve CPU utilization for: " + resourceUtilizationRequest.getResourceName(), e);
            throw new ResourceServiceUtilizationException("Could not retrieve CPU utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public CPUUtilizationResult retrieveCPUUtilizationForNode(JsonNode json) {
        JsonNode node = findNodeJsonNode(json);
        String name = node.get("nodeName").asText();
        String time = node.get("cpu").get("time").asText();
        long usageNanoCores = node.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = node.get("cpu").get("usageCoreNanoSeconds").asLong();
        float usagePercent = getUsagePercent((double) usageNanoCores);

        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
                .usageCPUPercentage(usagePercent)
                .build();

        CPUUtilizationResult cpuUtilizationResult = CPUUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .cpuUtilizationMeasurement(cpuUtilizationMeasurement)
                .build();
        return cpuUtilizationResult;
    }

    // Assumption, only one container per pod
    public CPUUtilizationResult retrieveCPUUtilizationForPod(JsonNode jsonNode, String name) throws ResourceServiceUtilizationException {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("cpu").get("time").asText();
        long usageNanoCores = pod.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = pod.get("cpu").get("usageCoreNanoSeconds").asLong();
        float usagePercent = getUsagePercent((double) usageNanoCores);
        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
                .usageCPUPercentage(usagePercent)
                .build();
        CPUUtilizationResult cpuUtilizationResult = CPUUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .cpuUtilizationMeasurement(cpuUtilizationMeasurement)
                .build();
        return cpuUtilizationResult;
    }

    public MemoryUtilizationResult retrieveMemoryUtilization(ResourceUtilizationRequest resourceUtilizationRequest) throws ResourceServiceUtilizationException {
        logger.debug("Retrieving memory utilization for: " + resourceUtilizationRequest.getResourceName());
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveMemoryUtilizationForNode(jsonNode);
            } else {
                // rewrite for pulceo node components
                if (resourceUtilizationRequest.getResourceName().endsWith("-pulceo-node-agent")) {
                    MemoryUtilizationResult memoryUtilizationResult = retrieveMemoryUtilizationForPod(jsonNode, "pulceo-node-agent");
                    memoryUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return memoryUtilizationResult;
                } else if (resourceUtilizationRequest.getResourceName().endsWith("-traefik")) {
                    MemoryUtilizationResult memoryUtilizationResult = retrieveMemoryUtilizationForPod(jsonNode, "traefik");
                    memoryUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return memoryUtilizationResult;
                } else {
                    return retrieveMemoryUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
                }
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new ResourceServiceUtilizationException("Could not retrieve memory utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public MemoryUtilizationResult retrieveMemoryUtilizationForNode(JsonNode jsonNode) {
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        String time = node.get("memory").get("time").asText();
        // TODO: fix here correct naming of capacitites
        long usageBytes = node.get("memory").get("workingSetBytes").asLong();
        long availableBytes = node.get("memory").get("availableBytes").asLong();
        float usageMemoryPercentage = getUsageMemoryPercentage((float) usageBytes);

        MemoryUtilizationMeasurement memoryUtilizationMeasurement = MemoryUtilizationMeasurement.builder()
                .time(time)
                .usageBytes(usageBytes)
                .availableBytes(availableBytes)
                .usageMemoryPercentage(usageMemoryPercentage)
                .build();
        MemoryUtilizationResult memoryUtilizationResult = MemoryUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .memoryUtilizationMeasurement(memoryUtilizationMeasurement)
                .build();

        return memoryUtilizationResult;
    }

    public MemoryUtilizationResult retrieveMemoryUtilizationForPod(JsonNode jsonNode, String name) throws ResourceServiceUtilizationException {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("memory").get("time").asText();
        long usageBytes = pod.get("memory").get("usageBytes").asLong();
        long availableBytes = Math.round(MEMORY_CAPACITY * 1024 * 1024 * 1024);
        float usageMemoryPercentage = getUsageMemoryPercentage((float) usageBytes);
        MemoryUtilizationMeasurement memoryUtilizationMeasurement = MemoryUtilizationMeasurement.builder()
                .time(time)
                .usageBytes(usageBytes)
                .availableBytes(availableBytes)
                .usageMemoryPercentage(usageMemoryPercentage)
                .build();
        MemoryUtilizationResult memoryUtilizationResult = MemoryUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .memoryUtilizationMeasurement(memoryUtilizationMeasurement)
                .build();
        return memoryUtilizationResult;
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationResult(ResourceUtilizationRequest resourceUtilizationRequest) throws ResourceServiceUtilizationException {
        logger.debug("Retrieving network utilization for: " + resourceUtilizationRequest.getResourceName());
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveNetworkUtilizationForNode(jsonNode);
            } else {
                // rewrite for pulceo node components
                if (resourceUtilizationRequest.getResourceName().endsWith("-pulceo-node-agent")) {
                    NetworkUtilizationResult networkUtilizationResult = retrieveNetworkUtilizationForPod(jsonNode, "pulceo-node-agent");
                    networkUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return networkUtilizationResult;
                } else if (resourceUtilizationRequest.getResourceName().endsWith("-traefik")) {
                    NetworkUtilizationResult networkUtilizationResult = retrieveNetworkUtilizationForPod(jsonNode, "traefik");
                    networkUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return networkUtilizationResult;
                } else {
                    return retrieveNetworkUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
                }
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new ResourceServiceUtilizationException("Could not retrieve network utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForNode(JsonNode jsonNode) {
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        String time = node.get("network").get("time").asText();
        String iface = node.get("network").get("name").asText();

        long rxBytes = 0;
        long txBytes = 0;

        JsonNode interfaces = node.get("network").get("interfaces");
        for (JsonNode networkIface : interfaces) {
            if (networkIface.get("name").asText().startsWith("eth")) {
                rxBytes += networkIface.get("rxBytes").asLong();
                txBytes += networkIface.get("txBytes").asLong();
            }
        }

        NetworkUtilizationMeasurement networkUtilizationMeasurement = NetworkUtilizationMeasurement.builder()
                .time(time)
                .iface(iface)
                .rxBytes(rxBytes)
                .txBytes(txBytes)
                .build();

        NetworkUtilizationResult networkUtilizationResult = NetworkUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .networkUtilizationMeasurement(networkUtilizationMeasurement)
                .build();

        return networkUtilizationResult;
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForPod(JsonNode jsonNode, String name) throws ResourceServiceUtilizationException {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("network").get("time").asText();
        String iface = pod.get("network").get("name").asText();
        long rxBytes = pod.get("network").get("rxBytes").asLong();
        long txBytes = pod.get("network").get("txBytes").asLong();
        NetworkUtilizationMeasurement networkUtilizationMeasurement = NetworkUtilizationMeasurement.builder()
                .time(time)
                .iface(iface)
                .rxBytes(rxBytes)
                .txBytes(txBytes)
                .build();
        NetworkUtilizationResult networkUtilizationResult = NetworkUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .networkUtilizationMeasurement(networkUtilizationMeasurement)
                .build();
        return networkUtilizationResult;
    }

    public StorageUtilizationResult retrieveStorageUtilizationResult(ResourceUtilizationRequest resourceUtilizationRequest) throws ResourceServiceUtilizationException {
        logger.debug("Retrieving storage utilization for: " + resourceUtilizationRequest.getResourceName());
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveStorageUtilizationForNode(jsonNode);
            } else {
                // rewrite for pulceo node components
                if (resourceUtilizationRequest.getResourceName().endsWith("-pulceo-node-agent")) {
                    StorageUtilizationResult storageUtilizationResult = retrieveStorageUtilizationForPod(jsonNode, "pulceo-node-agent");
                    storageUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return storageUtilizationResult;
                } else if (resourceUtilizationRequest.getResourceName().endsWith("-traefik")) {
                    StorageUtilizationResult storageUtilizationResult = retrieveStorageUtilizationForPod(jsonNode, "traefik");
                    storageUtilizationResult.setResourceName(resourceUtilizationRequest.getResourceName());
                    return storageUtilizationResult;
                } else {
                    return retrieveStorageUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
                }
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new ResourceServiceUtilizationException("Could not retrieve network utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public StorageUtilizationResult retrieveStorageUtilizationForNode(JsonNode jsonNode) {
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        long capacityBytes = node.get("fs").get("capacityBytes").asLong();
        long usedBytes = node.get("fs").get("usedBytes").asLong();
        float usageStoragePercentage = (float) Math.round(((float) usedBytes / capacityBytes) * 10000) / 10000 * 100;

        StorageUtilizationMeasurement storageUtilizationMeasurement = StorageUtilizationMeasurement.builder()
                .time(node.get("fs").get("time").asText())
                .name(name + "-fs")
                .usedBytes(usedBytes)
                .capacityBytes(capacityBytes)
                .usageStoragePercentage(usageStoragePercentage)
                .build();

        StorageUtilizationResult storageUtilizationResult = StorageUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(node.get("fs").get("time").asText())
                .storageUtilizationMeasurement(storageUtilizationMeasurement)
                .build();

        return storageUtilizationResult;
    }

    public StorageUtilizationResult retrieveStorageUtilizationForPod(JsonNode jsonNode, String name) throws ResourceServiceUtilizationException {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        long capacityBytes = pod.get("ephemeral-storage").get("capacityBytes").asLong();
        long usedBytes = 0;
        JsonNode volumes = pod.get("volume");
        for (JsonNode v : volumes) {
            usedBytes += v.get("usedBytes").asLong();
        }

        StorageUtilizationMeasurement storageUtilizationMeasurement = StorageUtilizationMeasurement.builder()
                .time(pod.get("ephemeral-storage").get("time").asText())
                .name(name + "-volumes")
                .usedBytes(usedBytes)
                .capacityBytes(capacityBytes)
                .build();

        StorageUtilizationResult storageUtilizationResult = StorageUtilizationResult.builder()
                .sourceHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(pod.get("ephemeral-storage").get("time").asText())
                .storageUtilizationMeasurement(storageUtilizationMeasurement)
                .build();

        return storageUtilizationResult;
    }

    private JsonNode findNodeJsonNode(JsonNode jsonNode) {
        return jsonNode.get("node");
    }

    private JsonNode findPodJsonNode(JsonNode jsonNode, String name) throws ResourceServiceUtilizationException {
        JsonNode podsNode = jsonNode.get("pods");
        for (JsonNode podNode : podsNode) {
            JsonNode podRefNode = podNode.get("podRef");
            String podName = podRefNode.get("name").asText();
            if (podName.startsWith(name)) {
                return podNode;
            }
        }
        // TODO: Replace with proper execeptionhandling
        throw new ResourceServiceUtilizationException("Pod not found: " + name);
    }

    private float getUsagePercent(double usageNanoCores) {
        return (float) Math.round((usageNanoCores / (LOGICAL_CPU_CORES * 10000000) * 100)) / 100;
    }

    private float getUsageMemoryPercentage(float usageBytes) {
        return (float) Math.round((usageBytes / (MEMORY_CAPACITY * 1024 * 1024 * 1024)) * 10000) / 10000 * 100;
    }

}
