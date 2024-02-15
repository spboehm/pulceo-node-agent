package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pulceo.pna.model.resources.*;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ResourceUtilizationServiceIntegrationTests {

    @Autowired
    ResourceUtilizationService resourceUtilizationService;

    private static JsonNode jsonNode;

    @BeforeAll
    public static void setup() throws IOException {
        File statsSummaryFile = new File("src/test/java/dev/pulceo/pna/resources/k8s/kubelet-stats-summary.json");
        ResourceUtilizationServiceIntegrationTests.jsonNode = Json.mapper().readTree(statsSummaryFile);
    }

    @Test
    public void testRetrieveCPUUtilizationForPod() {
        // given
        String name = "my-nginx";
        CPUUtilizationResult expectedCPUUtilizationResult = CPUUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.POD)
                .resourceName("my-nginx")
                .time("22024-02-15T13:18:56Z")
                .cpuUtilizationMeasurement(CPUUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:56Z")
                        .usageNanoCores(0)
                        .usageCoreNanoSeconds(72698000)
                        .build())
                .build();

        // when
        CPUUtilizationResult cpuUtilizationResult = this.resourceUtilizationService.retrieveCPUUtilizationForPod(ResourceUtilizationServiceIntegrationTests.jsonNode, name);

        // then
        assertEquals(expectedCPUUtilizationResult.getCpuUtilizationMeasurement(), cpuUtilizationResult.getCpuUtilizationMeasurement());
    }

    @Test
    public void testRetrieveMemoryUtilizationForPod() {
        // given
        String name = "my-nginx";
        MemoryUtilizationResult expectedMemoryUtilizationResult = MemoryUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.POD)
                .resourceName("my-nginx")
                .time("2024-02-15T13:18:56Z")
                .memoryUtilizationMeasurement(MemoryUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:56Z")
                        .usageBytes(14315520)
                        .build())
                .build();

        // when
        MemoryUtilizationResult memoryUtilizationResult = this.resourceUtilizationService.retrieveMemoryUtilizationForPod(ResourceUtilizationServiceIntegrationTests.jsonNode, name);

        // then
        assertEquals(expectedMemoryUtilizationResult.getMemoryUtilizationMeasurement(), memoryUtilizationResult.getMemoryUtilizationMeasurement());
    }

    @Test
    public void testRetrieveNetworkUtilizationForPod() {
        // given
        String name = "my-nginx";
        NetworkUtilizationResult expectedNetworkUtilizationResult = NetworkUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.POD)
                .resourceName("my-nginx")
                .time("2024-02-15T13:19:04Z")
                .networkUtilizationMeasurement(NetworkUtilizationMeasurement.builder()
                        .time("2024-02-15T13:19:04Z")
                        .iface("eth0")
                        .rxBytes(850)
                        .txBytes(892)
                        .build())
                .build();

        // when
        NetworkUtilizationResult networkUtilizationResult = this.resourceUtilizationService.retrieveNetworkUtilizationForPod(ResourceUtilizationServiceIntegrationTests.jsonNode, name);

        // then
        assertEquals(expectedNetworkUtilizationResult.getNetworkUtilizationMeasurement(), networkUtilizationResult.getNetworkUtilizationMeasurement());
    }

    @Test
    public void testRetrieveDiskUtilizationForPod() {
        // given
        String name = "my-nginx";
        StorageUtilizationResult expectedDiskUtilizationResult = StorageUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.POD)
                .resourceName("my-nginx")
                .time("2024-02-15T13:19:07Z")
                .storageUtilizationMeasurement(StorageUtilizationMeasurement.builder()
                        .time("2024-02-15T13:19:07Z")
                        .name("my-nginx-volumes")
                        .usedBytes(BigInteger.valueOf(12288))
                        .capacityBytes(new BigInteger("723957841920"))
                        .build())
                .build();

        // when
        StorageUtilizationResult diskUtilizationResult = this.resourceUtilizationService.retrieveStorageUtilizationForFod(ResourceUtilizationServiceIntegrationTests.jsonNode, name);

        // then
        assertEquals(expectedDiskUtilizationResult.getStorageUtilizationMeasurement(), diskUtilizationResult.getStorageUtilizationMeasurement());
    }

    @Test
    public void testRetrieveCPUUtilizationForNode() {
        // given
        String name = "k3d-pna-test-server-0";
        CPUUtilizationResult expectedCPUUtilizationResult = CPUUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time("2024-02-15T13:18:57Z")
                .cpuUtilizationMeasurement(CPUUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:57Z")
                        .usageNanoCores(88914290)
                        .usageCoreNanoSeconds(0)
                        .build())
                .build();

        // when
        CPUUtilizationResult cpuUtilizationResult = this.resourceUtilizationService.retrieveCPUUtilizationForNode(ResourceUtilizationServiceIntegrationTests.jsonNode);

        // then
        assertEquals(expectedCPUUtilizationResult.getCpuUtilizationMeasurement(), cpuUtilizationResult.getCpuUtilizationMeasurement());
    }
}
