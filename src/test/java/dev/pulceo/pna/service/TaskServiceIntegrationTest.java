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
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    @Autowired
    private PublishSubscribeChannel proxyMessageChannel;

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
    public void createAndScheduleTasks() throws Exception {
        // given
        int batchSize = 1000;
        List<Task> tasks = generateTasks(batchSize);
        List<Task> createdTasks = new ArrayList<>();
        CountDownLatch sentMessages = new CountDownLatch(batchSize * 3); // running + completed
        proxyMessageChannel.subscribe(message -> {
            sentMessages.countDown();
        });

        // when
        for (Task task : tasks) {
            Task createdTask = this.taskService.createTask(task);
            createdTasks.add(createdTask);
            this.taskService.queueForScheduling(task);
        }

        // ensure all tasks are in NEW status
        while (this.taskProcessor.getTaskCounterNew().intValue() != batchSize) {
            Thread.sleep(100);
        }

        // simulate eis clients processing tasks concurrently
        ExecutorService executorService = Executors.newFixedThreadPool(batchSize);
        CountDownLatch runningTasks = new CountDownLatch(batchSize);
        CountDownLatch completedTasks = new CountDownLatch(batchSize);
        simulateEisClients(createdTasks, executorService, runningTasks, completedTasks);
        executorService.shutdown();
        shutdownAndAwaitTermination(executorService);

        // wait after all tasks are processed by eis clients
        boolean runningTasksValid = runningTasks.await(30, TimeUnit.SECONDS);
        boolean completedTasksValid = completedTasks.await(30, TimeUnit.SECONDS);
        boolean processingCompleted = sentMessages.await(30, TimeUnit.SECONDS);

        // then
        assertTrue(runningTasksValid);
        assertTrue(completedTasksValid);
        assertTrue(processingCompleted);
        assertEquals(batchSize, this.taskProcessor.getTaskCounterRunning().intValue());
        assertEquals(batchSize, this.taskProcessor.getTaskCounterCompleted().intValue());
    }

    private void simulateEisClients(List<Task> createdTasks, ExecutorService executorService, CountDownLatch runningTasks, CountDownLatch completedTasks) {
        for (Task createdTask : createdTasks) {
            executorService.submit(() -> {
                try {
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
                    Thread.sleep((long)(Math.random() * 100));

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
                    completedTasks.countDown();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });
        }
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

    void shutdownAndAwaitTermination(ExecutorService pool) {
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
