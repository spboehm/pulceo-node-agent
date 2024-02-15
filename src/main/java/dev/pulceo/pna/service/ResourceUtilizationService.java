package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.model.resources.*;
import io.swagger.v3.core.util.Json;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResourceUtilizationService {

    private NodeService nodeService;

    @Autowired
    public ResourceUtilizationService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    ObjectMapper objectMapper = new ObjectMapper();

    // Scope: node, application, for all application components automatically

    // TODO: curl kubelet api and retrieve the json

    private void retrieveStatsSummaryToken() {
        // curl ...

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

    public void retrieveCPUUtilizationForNode(Json json, String name) {

    }



}
