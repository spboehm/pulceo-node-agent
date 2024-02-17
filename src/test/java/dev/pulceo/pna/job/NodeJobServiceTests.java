package dev.pulceo.pna.job;

import dev.pulceo.pna.model.jobs.NodeJob;
import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import dev.pulceo.pna.model.resources.K8sResourceType;
import dev.pulceo.pna.model.resources.ResourceUtilizationRequest;
import dev.pulceo.pna.model.resources.ResourceUtilizationType;
import dev.pulceo.pna.repository.JobRepository;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.ResourceUtilizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NodeJobServiceTests {

    @Autowired
    JobRepository jobRepository;

    @Autowired
    JobService jobService;

    @Autowired
    ResourceUtilizationService resourceUtilizationService;

    @Value("${k3s.nodename}")
    private String nodeName;

    @BeforeEach
    @AfterEach
    public void reset() {
        this.jobRepository.deleteAll();
    }

    @Test
    public void testCreateResourceUtilizationJob() {
        // given
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.CPU_UTIL)
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(nodeName)
                .build();

        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.CPU_UTIL)
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(1)
                .build();

        // when
        NodeJob createdNodeJob = this.jobService.createNodeJob(resourceUtilizationJob);

        // then
        assertEquals(createdNodeJob, resourceUtilizationJob);
    }





}
