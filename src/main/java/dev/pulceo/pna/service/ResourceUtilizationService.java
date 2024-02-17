package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.exception.ResourceServiceUtilizationException;
import dev.pulceo.pna.model.resources.*;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResourceUtilizationService {

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

    @Autowired
    public ResourceUtilizationService(NodeService nodeService) {
        this.nodeService = nodeService;
        LOGICAL_CPU_CORES = this.nodeService.readLocalNode().get().getCpuResource().getCpuCapacity().getThreads();
        MEMORY_CAPACITY = this.nodeService.readLocalNode().get().getMemoryResource().getMemoryCapacity().getSize();
    }

    public JsonNode readStatSummaryFromKubelet() throws ResourceServiceUtilizationException {
        // TODO: read token
        try {
            String token = Files.readString(Path.of(API_SERVICE_ACCOUNT_TOKEN_PATH));
            ProcessBuilder processBuilder = new ProcessBuilder("curl" , "--cacert", API_SERVICE_ACOUNT_CA_CERT_PATH, "--header", "Authorization: Bearer " + token, "-X", "GET", "https://" + API_SERVER_HOST + ":" + API_SERVER_PORT + "/api/v1/nodes/" + K3S_NODENAME + "/proxy/stats/summary");
            Process process = processBuilder.start();
            process.waitFor();
            List<String> strings =  ProcessUtils.readProcessOutput(process.getInputStream());
            return this.objectMapper.readTree(strings.stream().collect(Collectors.joining("\n")));
        } catch (IOException | ProcessException e) {
            throw new ResourceServiceUtilizationException("Could not read measurement from kubelet", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CPUUtilizationResult retrieveCPUUtilization(ResourceUtilizationRequest resourceUtilizationRequest) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveCPUUtilizationForNode(jsonNode);
            } else {
                return retrieveCPUUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve CPU utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    // Assumption, only one container per pod
    public CPUUtilizationResult retrieveCPUUtilizationForPod(JsonNode jsonNode, String name) {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("cpu").get("time").asText();
        long usageNanoCores = pod.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = pod.get("cpu").get("usageCoreNanoSeconds").asLong();
        float usagePercent = (float) Math.round(((double) usageNanoCores / (LOGICAL_CPU_CORES * 1000000) * 100)) / 100;
        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
                .usageCPUPercentage(usagePercent)
                .build();
        CPUUtilizationResult cpuUtilizationResult = CPUUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .cpuUtilizationMeasurement(cpuUtilizationMeasurement)
                .build();
        return cpuUtilizationResult;
    }

    public MemoryUtilizationResult retrieveMemoryUtilization(ResourceUtilizationRequest resourceUtilizationRequest) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveMemoryUtilizationForNode(jsonNode);
            } else {
                return retrieveMemoryUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve memory utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public MemoryUtilizationResult retrieveMemoryUtilizationForPod(String name) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveMemoryUtilizationForPod(jsonNode, name);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve memory utilization for pod: " + name, e);
        }
    }

    public MemoryUtilizationResult retrieveMemoryUtilizationForPod(JsonNode jsonNode, String name) {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("memory").get("time").asText();
        long usageBytes = pod.get("memory").get("usageBytes").asLong();
        MemoryUtilizationMeasurement memoryUtilizationMeasurement = MemoryUtilizationMeasurement.builder()
                .time(time)
                .usageBytes(usageBytes)
                .build();
        MemoryUtilizationResult memoryUtilizationResult = MemoryUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .memoryUtilizationMeasurement(memoryUtilizationMeasurement)
                .build();
        return memoryUtilizationResult;
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForPod(String name) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveNetworkUtilizationForPod(jsonNode, name);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve network utilization for pod: " + name, e);
        }
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationResult(ResourceUtilizationRequest resourceUtilizationRequest) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            if (resourceUtilizationRequest.getK8sResourceType() == K8sResourceType.NODE) {
                return retrieveNetworkUtilizationForNode(jsonNode);
            } else {
                return retrieveNetworkUtilizationForPod(jsonNode, resourceUtilizationRequest.getResourceName());
            }
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve network utilization for: " + resourceUtilizationRequest.getResourceName(), e);
        }
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForPod(JsonNode jsonNode, String name) {
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
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.POD)
                .resourceName(name)
                .time(time)
                .networkUtilizationMeasurement(networkUtilizationMeasurement)
                .build();
        return networkUtilizationResult;
    }

    public StorageUtilizationResult retrieveStorageUtilizationForPod(String name) {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveStorageUtilizationForFod(jsonNode, name);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve storage utilization for pod: " + name, e);
        }
    }

    public StorageUtilizationResult retrieveStorageUtilizationForFod(JsonNode jsonNode, String name) {
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
                .srcHost(this.nodeService.readLocalNode().get().getHost())
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

    private JsonNode findPodJsonNode(JsonNode jsonNode, String name) {
        JsonNode podsNode = jsonNode.get("pods");
        for (JsonNode podNode : podsNode) {
            JsonNode podRefNode = podNode.get("podRef");
            String podName = podRefNode.get("name").asText();
            if (podName.startsWith(name)) {
                return podNode;
            }
        }
        // TODO: Replace with proper execeptionhandling
        throw new RuntimeException("Pod not found: " + name);
    }

    public CPUUtilizationResult retrieveCPUUtilizationForNode() {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveCPUUtilizationForNode(jsonNode);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve CPU utilization for node", e);
        }
    }

    public CPUUtilizationResult retrieveCPUUtilizationForNode(JsonNode json) {
        JsonNode node = findNodeJsonNode(json);
        String name = node.get("nodeName").asText();
        String time = node.get("cpu").get("time").asText();
        long usageNanoCores = node.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = node.get("cpu").get("usageCoreNanoSeconds").asLong();
        float usagePercent = (float) Math.round(((double) usageNanoCores / (LOGICAL_CPU_CORES * 1000000) * 100)) / 100;

        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
                .usageCPUPercentage(usagePercent)
                .build();

        CPUUtilizationResult cpuUtilizationResult = CPUUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .cpuUtilizationMeasurement(cpuUtilizationMeasurement)
                .build();
        return cpuUtilizationResult;
    }



    public MemoryUtilizationResult retrieveMemoryUtilizationForNode() {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveMemoryUtilizationForNode(jsonNode);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve memory utilization for node", e);
        }
    }

    public MemoryUtilizationResult retrieveMemoryUtilizationForNode(JsonNode jsonNode) {
        System.out.println(jsonNode.toPrettyString());
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        String time = node.get("memory").get("time").asText();
        long usageBytes = node.get("memory").get("usageBytes").asLong();
        long availableBytes = node.get("memory").get("availableBytes").asLong();
        float usageMemoryPercentage = (float) Math.round(((float) usageBytes / (MEMORY_CAPACITY * 1024 * 1024 * 1024)) * 10000) / 10000 * 100;

        MemoryUtilizationMeasurement memoryUtilizationMeasurement = MemoryUtilizationMeasurement.builder()
                .time(time)
                .usageBytes(usageBytes)
                .availableBytes(availableBytes)
                .usageMemoryPercentage(usageMemoryPercentage)
                .build();
        MemoryUtilizationResult memoryUtilizationResult = MemoryUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .memoryUtilizationMeasurement(memoryUtilizationMeasurement)
                .build();

        return memoryUtilizationResult;
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForNode() {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveNetworkUtilizationForNode(jsonNode);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve network utilization for node", e);
        }
    }

    public NetworkUtilizationResult retrieveNetworkUtilizationForNode(JsonNode jsonNode) {
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        String time = node.get("network").get("time").asText();
        String iface = node.get("network").get("name").asText();
        long rxBytes = node.get("network").get("rxBytes").asLong();
        long txBytes = node.get("network").get("txBytes").asLong();

        NetworkUtilizationMeasurement networkUtilizationMeasurement = NetworkUtilizationMeasurement.builder()
                .time(time)
                .iface(iface)
                .rxBytes(rxBytes)
                .txBytes(txBytes)
                .build();

        NetworkUtilizationResult networkUtilizationResult = NetworkUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(time)
                .networkUtilizationMeasurement(networkUtilizationMeasurement)
                .build();

        return networkUtilizationResult;
    }

    public StorageUtilizationResult retrieveStorageUtilizationForNode() {
        try {
            JsonNode jsonNode = readStatSummaryFromKubelet();
            return retrieveStorageUtilizationForNode(jsonNode);
        } catch (ResourceServiceUtilizationException e) {
            throw new RuntimeException("Could not retrieve storage utilization for node", e);
        }
    }

    public StorageUtilizationResult retrieveStorageUtilizationForNode(JsonNode jsonNode) {
        JsonNode node = findNodeJsonNode(jsonNode);
        String name = node.get("nodeName").asText();
        long capacityBytes = node.get("fs").get("capacityBytes").asLong();
        long usedBytes = node.get("fs").get("usedBytes").asLong();
        float usageMemoryPercentage = (float) Math.round(((float) usedBytes / capacityBytes) * 10000) / 10000 * 100;

        StorageUtilizationMeasurement storageUtilizationMeasurement = StorageUtilizationMeasurement.builder()
                .time(node.get("fs").get("time").asText())
                .name(name + "-fs")
                .usedBytes(usedBytes)
                .capacityBytes(capacityBytes)
                .usageStoragePercentage(usageMemoryPercentage)
                .build();

        StorageUtilizationResult storageUtilizationResult = StorageUtilizationResult.builder()
                .srcHost(this.nodeService.readLocalNode().get().getHost())
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time(node.get("fs").get("time").asText())
                .storageUtilizationMeasurement(storageUtilizationMeasurement)
                .build();

        return storageUtilizationResult;
    }

}
