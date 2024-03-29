package dev.pulceo.pna.job;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.jobs.NodeJob;
import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import dev.pulceo.pna.model.message.Message;
import dev.pulceo.pna.model.message.NetworkMetric;
import dev.pulceo.pna.model.resources.*;
import dev.pulceo.pna.repository.JobRepository;
import dev.pulceo.pna.service.JobService;
import dev.pulceo.pna.service.ResourceUtilizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.PublishSubscribeChannel;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NodeJobServiceTests {

    @Autowired
    PublishSubscribeChannel resourceUtilizationCPUServiceMessageChannel;

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

    @BeforeAll
    public static void setUp() throws InterruptedException, IOException {
        // given
        ProcessBuilder processBuilder = new ProcessBuilder("sh", "-c", "./bootstrap-k3s-access.sh");
        Process p = processBuilder.start();
        p.waitFor();
    }

    @Test
    public void testCreateResourceUtilizationJobTwithCPU() {
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
        NodeJob createdNodeJob = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob);

        // then
        assertEquals(createdNodeJob, resourceUtilizationJob);
    }

    @Test
    public void testScheduleResourceUtilizationJobWithCPU() throws JobServiceException, InterruptedException {
        // given
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.CPU_UTIL)
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(nodeName)
                .build();

        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.CPU_UTIL)
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(15)
                .build();
        long id = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob).getId();

        // when
        long localJobId = this.jobService.scheduleResourceUtilizationJobForCPU(id);
        BlockingQueue<Message> cpuUtilizationResult = new ArrayBlockingQueue<>(100);
        this.resourceUtilizationCPUServiceMessageChannel.subscribe(message -> cpuUtilizationResult.add((Message) message.getPayload()));

        // initiate orderly shutdown
        this.jobService.cancelJob(localJobId);
        Message message = cpuUtilizationResult.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        CPUUtilizationMeasurement cpuUtilizationMeasurement = (CPUUtilizationMeasurement) map.get("cpuUtilizationMeasurement");

        // then
        assertNotNull(map);
        assert("127.0.0.1".equals(map.get("sourceHost")));
        assertTrue(cpuUtilizationMeasurement.getUsageCPUPercentage() >= 0.0f);
        assertTrue(cpuUtilizationMeasurement.getUsageNanoCores() >= 0.0f);
        assertTrue(cpuUtilizationMeasurement.getUsageCoreNanoSeconds() >= 0.0f);
    }

    @Test
    public void testScheduleResourceUtilizationJobWithMem() throws JobServiceException, InterruptedException {
        // given
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.MEM_UTIL)
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(nodeName)
                .build();

        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.MEM_UTIL)
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(15)
                .build();
        long id = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob).getId();

        // when
        long localJobId = this.jobService.scheduleResourceUtilizationJobForMEM(id);
        BlockingQueue<Message> memoryUtilizationBlockingQueue = new ArrayBlockingQueue<>(100);
        this.resourceUtilizationCPUServiceMessageChannel.subscribe(message -> memoryUtilizationBlockingQueue.add((Message) message.getPayload()));

        // initiate orderly shutdown
        this.jobService.cancelJob(localJobId);
        Message message = memoryUtilizationBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        MemoryUtilizationMeasurement memoryUtilizationMeasurement = (MemoryUtilizationMeasurement) map.get("memoryUtilizationMeasurement");

        // then
        assertNotNull(map);
        assert("127.0.0.1".equals(map.get("sourceHost")));

        assertTrue(memoryUtilizationMeasurement.getUsageMemoryPercentage() >= 0.0f);
        assertTrue(memoryUtilizationMeasurement.getAvailableBytes() > 0);
        assertTrue(memoryUtilizationMeasurement.getUsageMemoryPercentage() > 0);
    }

    @Test
    public void testScheduleResourceUtilizationJobWithNetwork() throws JobServiceException, InterruptedException {
        // given
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.NET_UTIL)
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(nodeName)
                .build();

        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.NET_UTIL)
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(15)
                .build();
        long id = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob).getId();

        // when
        long localJobId = this.jobService.scheduleResourceUtilizationJobForNetwork(id);
        BlockingQueue<Message> networkUtilizationBlockingQueue = new ArrayBlockingQueue<>(100);
        this.resourceUtilizationCPUServiceMessageChannel.subscribe(message -> networkUtilizationBlockingQueue.add((Message) message.getPayload()));

        // initiate orderly shutdown
        this.jobService.cancelJob(localJobId);
        Message message = networkUtilizationBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        NetworkUtilizationMeasurement networkUtilizationMeasurement = (NetworkUtilizationMeasurement) map.get("networkUtilizationMeasurement");

        // then
        assertNotNull(map);
        assert("127.0.0.1".equals(map.get("sourceHost")));

        assertTrue(networkUtilizationMeasurement.getRxBytes() > 0);
        assertTrue(networkUtilizationMeasurement.getTxBytes() > 0);
    }

    @Test
    public void testScheduleResourceUtilizationJobWithStorage() throws JobServiceException, InterruptedException {
        // given
        ResourceUtilizationRequest resourceUtilizationRequest = ResourceUtilizationRequest.builder()
                .resourceUtilizationType(ResourceUtilizationType.STORAGE_UTIL)
                .k8sResourceType(K8sResourceType.NODE)
                .resourceName(nodeName)
                .build();

        ResourceUtilizationJob resourceUtilizationJob = ResourceUtilizationJob.builder()
                .resourceUtilizationType(ResourceUtilizationType.STORAGE_UTIL)
                .resourceUtilizationRequest(resourceUtilizationRequest)
                .recurrence(15)
                .build();
        long id = this.jobService.createNodeResourceUtilizationJob(resourceUtilizationJob).getId();

        // when
        long localJobId = this.jobService.scheduleResourceUtilizationJobForStorage(id);
        BlockingQueue<Message> storageUtilizationBlockingQueue = new ArrayBlockingQueue<>(100);
        this.resourceUtilizationCPUServiceMessageChannel.subscribe(message -> storageUtilizationBlockingQueue.add((Message) message.getPayload()));

        // initiate orderly shutdown
        this.jobService.cancelJob(localJobId);
        Message message = storageUtilizationBlockingQueue.take();

        NetworkMetric networkMetric = (NetworkMetric) message.getMetric();
        Map<String, Object> map = networkMetric.getMetricResult().getResultData();
        StorageUtilizationMeasurement storageUtilizationMeasurement = (StorageUtilizationMeasurement) map.get("storageUtilizationMeasurement");

        // then
        assertNotNull(map);
        assert("127.0.0.1".equals(map.get("sourceHost")));
        assert(storageUtilizationMeasurement.getUsageStoragePercentage() > 0.0f);
    }

}
