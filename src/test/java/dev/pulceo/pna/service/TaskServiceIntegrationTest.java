package dev.pulceo.pna.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import dev.pulceo.pna.dto.task.internal.UpdateTaskInternallyOnPNADTO;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.repository.TaskRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskProcessor taskProcessor;

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TaskRepository taskRepository;

    private final static WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration.options().httpsPort(8087));

    @BeforeEach
    public void setupEach() {
        taskRepository.deleteAll();
    }

    @BeforeAll
    public static void setup() {
        wireMockServer.start();
        wireMockServer.stubFor(post(urlPathMatching(".*/tasks"))
                .willReturn(aResponse()
                        .withStatus(200)));
    }

    @AfterAll
    public static void teardown() {
        wireMockServer.shutdown();
    }

    @Test
    public void createAndScheduleTenTasks() throws Exception {
        // given
        int batchSize = 10;
        List<Task> tasks = generateTasks(batchSize);
        List<Task> createdTasks = new ArrayList<>();


        // when
        for (Task task : tasks) {
            Task createdTask = this.taskService.createTask(task);
            createdTasks.add(createdTask);
            this.taskService.queueForScheduling(createdTask.getUuid().toString());
        }

        // 10 new
        while (this.taskProcessor.getTaskCounterNew().intValue() != batchSize) {
            Thread.sleep(100);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);
        CountDownLatch runningTasks = new CountDownLatch(batchSize);
        CountDownLatch completed = new CountDownLatch(batchSize);
        // 10 running
        // TODO: Update internally to task running and completed
        for (Task createdTask : createdTasks) {
            UpdateTaskInternallyOnPNADTO updateNewTaskInternallyOnPNADTO = UpdateTaskInternallyOnPNADTO.builder()
                    .taskId(createdTask.getUuid().toString())
                    .newTaskStatus(TaskStatus.RUNNING)
                    .progress(50.0f)
                    .comment("Task is running")
                    .build();

            this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/internal/tasks/" + updateNewTaskInternallyOnPNADTO.getTaskId())
                            .contentType("application/json")
                            .accept("application/json")
                            .content(this.objectMapper.writeValueAsString(updateNewTaskInternallyOnPNADTO)))
                    .andExpect(status().is2xxSuccessful());
            runningTasks.countDown();

            // simulate some delay
            Thread.sleep((long)(Math.random() * 1000));

            UpdateTaskInternallyOnPNADTO updateCompletedTaskInternallyOnPNADTO = UpdateTaskInternallyOnPNADTO.builder()
                    .taskId(createdTask.getUuid().toString())
                    .newTaskStatus(TaskStatus.COMPLETED)
                    .progress(50.0f)
                    .comment("Task is running")
                    .build();

            this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/internal/tasks/" + updateCompletedTaskInternallyOnPNADTO.getTaskId())
                            .contentType("application/json")
                            .accept("application/json")
                            .content(this.objectMapper.writeValueAsString(updateCompletedTaskInternallyOnPNADTO)))
                    .andExpect(status().is2xxSuccessful());
            completed.countDown();
        }
        executorService.shutdown();

        while (this.taskProcessor.getTaskCounterRunning().intValue() != batchSize) {
            Thread.sleep(100);
        }

        while (this.taskProcessor.getTaskCounterCompleted().intValue() != batchSize) {
            Thread.sleep(100);
        }

        assertEquals(this.taskProcessor.getTaskCounterRunning().intValue(), batchSize);
        assertEquals(this.taskProcessor.getTaskCounterCompleted().intValue(), batchSize);
    }

    private List<Task> generateTasks(int batchSize) {
        List<Task> tasks = new ArrayList<>();
        int taskSequenceNumber = 0;
        for (int i = 0; i < batchSize; i++) {
            Task task = Task.builder()
                    .taskSequenceNumber(taskSequenceNumber++)
                    .globalTaskUUID(UUID.randomUUID().toString())
                    .applicationUUID(UUID.randomUUID().toString())
                    .applicationComponentId("127.0.0.1:8087")
                    .payload(new byte[256])
                    .callbackProtocol("protocol")
                    .callbackEndpoint("endpoint")
                    .destinationApplicationComponentProtocol("destinationProtocol")
                    .destinationApplicationComponentEndpoint("destinationEndpoint")
                    .properties(Map.of("key1", "value1", "key2", "value2"))
                    .build();
            tasks.add(task);
        }
        return tasks;
    }


}
