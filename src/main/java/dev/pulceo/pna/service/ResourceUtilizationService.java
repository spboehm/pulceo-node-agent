package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pulceo.pna.model.resources.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceUtilizationService {

    private NodeService nodeService;
    // TODO: fix potentially happening NPE
    private final int CPU_CORES;

    @Autowired
    public ResourceUtilizationService(NodeService nodeService) {
        this.nodeService = nodeService;
        CPU_CORES = this.nodeService.readLocalNode().get().getCpuResource().getCpuCapacity().getCores();
    }

    // Assumption, only one container per pod
    public CPUUtilizationResult retrieveCPUUtilizationForPod(JsonNode jsonNode, String name) {
        JsonNode pod = findPodJsonNode(jsonNode, name);
        String time = pod.get("cpu").get("time").asText();
        long usageNanoCores = pod.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = pod.get("cpu").get("usageCoreNanoSeconds").asLong();
        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
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

    public CPUUtilizationResult retrieveCPUUtilizationForNode(JsonNode json) {
        JsonNode node = findNodeJsonNode(json);
        String name = node.get("nodeName").asText();
        String time = node.get("cpu").get("time").asText();
        long usageNanoCores = node.get("cpu").get("usageNanoCores").asLong();
        long usageCoreNanoSeconds = node.get("cpu").get("usageCoreNanoSeconds").asLong();
        float usagePercent = (float) Math.round(((double) usageNanoCores / (CPU_CORES * 1000000) * 100)) / 100;

        CPUUtilizationMeasurement cpuUtilizationMeasurement  = CPUUtilizationMeasurement.builder()
                .time(time)
                .usageNanoCores(usageNanoCores)
                .usageCoreNanoSeconds(usageCoreNanoSeconds)
                .usagePercentage(usagePercent)
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

}
