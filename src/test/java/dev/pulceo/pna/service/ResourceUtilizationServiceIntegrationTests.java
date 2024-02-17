package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pulceo.pna.exception.ResourceServiceUtilizationException;
import dev.pulceo.pna.model.resources.*;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

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

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(resourceUtilizationService, "CPU_CORES", 12);
        ReflectionTestUtils.setField(resourceUtilizationService, "MEMORY_CAPACITY", 15.322609f);
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
                        .usageCPUPercentage(0.00f)
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
                        .usedBytes(12288)
                        .capacityBytes(723957841920L)
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
                        .usageCoreNanoSeconds(645628635000L)
                        .usageCPUPercentage(7.41f)
                        .build())
                .build();

        // when
        CPUUtilizationResult cpuUtilizationResult = this.resourceUtilizationService.retrieveCPUUtilizationForNode(ResourceUtilizationServiceIntegrationTests.jsonNode);

        // then
        assertEquals(expectedCPUUtilizationResult.getCpuUtilizationMeasurement(), cpuUtilizationResult.getCpuUtilizationMeasurement());
    }

    @Test
    public void testRetrieveMemoryUtilizationForNode() {
        // given
        String name = "k3d-pna-test-server-0";
        MemoryUtilizationResult expectedMemoryUtilizationResult = MemoryUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time("2024-02-15T13:18:57Z")
                .memoryUtilizationMeasurement(MemoryUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:57Z")
                        .usageBytes(626122752)
                        .availableBytes(15880196096L)
                        .usageMemoryPercentage(3.94f)
                        .build())
                .build();

        // when
        MemoryUtilizationResult memoryUtilizationResult = this.resourceUtilizationService.retrieveMemoryUtilizationForNode(ResourceUtilizationServiceIntegrationTests.jsonNode);

        // then
        assertEquals(expectedMemoryUtilizationResult.getMemoryUtilizationMeasurement(), memoryUtilizationResult.getMemoryUtilizationMeasurement());
    }

    @Test
    public void testRetrieveNetworkUtilizationForNode() {
        // given
        String name = "k3d-pna-test-server-0";
        NetworkUtilizationResult expectedNetworkUtilizationResult = NetworkUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time("2024-02-15T13:18:57Z")
                .networkUtilizationMeasurement(NetworkUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:57Z")
                        .iface("eth0")
                        .rxBytes(134048355)
                        .txBytes(8865147)
                        .build())
                .build();

        // when
        NetworkUtilizationResult networkUtilizationResult = this.resourceUtilizationService.retrieveNetworkUtilizationForNode(ResourceUtilizationServiceIntegrationTests.jsonNode);

        // then
        assertEquals(expectedNetworkUtilizationResult.getNetworkUtilizationMeasurement(), networkUtilizationResult.getNetworkUtilizationMeasurement());
    }

    @Test
    public void testRetrieveDiskUtilizationForNode() {
        // given
        String name = "k3d-pna-test-server-0";
        StorageUtilizationResult expectedDiskUtilizationResult = StorageUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(name)
                .time("2024-02-15T13:18:57Z")
                .storageUtilizationMeasurement(StorageUtilizationMeasurement.builder()
                        .time("2024-02-15T13:18:57Z")
                        .name(name + "-fs")
                        .usedBytes(247234912256L)
                        .capacityBytes(723957841920L)
                        .usageStoragePercentage(34.15f)
                        .build())
                .build();

        // when
        StorageUtilizationResult diskUtilizationResult = this.resourceUtilizationService.retrieveStorageUtilizationForNode(ResourceUtilizationServiceIntegrationTests.jsonNode);

        // then
        assertEquals(expectedDiskUtilizationResult.getStorageUtilizationMeasurement(), diskUtilizationResult.getStorageUtilizationMeasurement());
    }

    @Test
    public void testReadFromKubelet() throws ResourceServiceUtilizationException, IOException, InterruptedException {
        // given
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "./bootstrap-k3s-access.sh");
        Process p = processBuilder.start();
        p.waitFor();

        // when
        JsonNode jsonNode = this.resourceUtilizationService.readStatSummaryFromKubelet();
        // then
        assertEquals("k3d-pna-test-server-0", jsonNode.get("node").get("nodeName").asText());
    }

    @Test
    public void testReadConcurrentlyFromKubelet() throws Exception {
        // given
        AtomicBoolean readingCompromised = new AtomicBoolean(false);
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        // when
        List<JsonNode> jsonNodeList = new ArrayList<>();
        for (int i= 0; i < 100; i++) {
            executorService.submit(() -> {
                try {
                    jsonNodeList.add(this.resourceUtilizationService.readStatSummaryFromKubelet());
                } catch (ResourceServiceUtilizationException e) {
                    readingCompromised.set(true);
                }
            });
        }
        executorService.shutdown();
        shutdownAndAwaitTermination(executorService);

        // then
        assertFalse(readingCompromised.get());
        assertTrue(jsonNodeList.stream().allMatch(jsonNode -> jsonNode.get("node").get("nodeName").asText().equals("k3d-pna-test-server-0")));
    }

    private static void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!pool.awaitTermination(60, TimeUnit.SECONDS))
                    System.err.println("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }
}
