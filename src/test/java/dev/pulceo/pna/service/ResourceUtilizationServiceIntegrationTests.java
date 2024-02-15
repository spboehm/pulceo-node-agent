package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.pulceo.pna.model.resources.CPUUtilizationMeasurement;
import dev.pulceo.pna.model.resources.CPUUtilizationResult;
import dev.pulceo.pna.model.resources.K8sResourceType;
import io.swagger.v3.core.util.Json;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;

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
    public void retrieveCPUUtilizationForPod() {
        // given
        String name = "my-nginx";
        CPUUtilizationResult expectedCPUUtilizationResult = CPUUtilizationResult.builder()
                .srcHost("127.0.0.1")
                .k8sResourceType(K8sResourceType.POD)
                .resourceName("my-nginx")
                .time("2024-02-15T13:11:23Z")
                .cpuUtilizationMeasurement(CPUUtilizationMeasurement.builder()
                        .time("2024-02-15T13:19:02Z")
                        .usageNanoCores(0)
                        .usageCoreNanoSeconds(56525000)
                        .build())
                .build();

        // when
        CPUUtilizationResult cpuUtilizationResult = this.resourceUtilizationService.retrieveCPUUtilizationForPod(ResourceUtilizationServiceIntegrationTests.jsonNode, name);

        // then
        assertEquals(expectedCPUUtilizationResult.getCpuUtilizationMeasurement(), cpuUtilizationResult.getCpuUtilizationMeasurement());
    }

}
