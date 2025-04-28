package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ProxyException;
import dev.pulceo.pna.exception.TaskServiceException;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.proxy.PSMProxy;
import dev.pulceo.pna.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Order(7)
@Service
public class TaskService implements ManagedService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final NodeService nodeService;
    // TODO: may configure as bean?
    private final BlockingQueue<String> taskQueue = new ArrayBlockingQueue<>(1000);
    private final PSMProxy psmProxy;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final WebClient webClient;
    private final TaskProcessor taskProcessor;


    @Autowired
    public TaskService(TaskRepository taskRepository, NodeService nodeService, PSMProxy psmProxy, ThreadPoolTaskExecutor threadPoolTaskExecutor, WebClient webClient, TaskProcessor taskProcessor) {
        this.taskRepository = taskRepository;
        this.nodeService = nodeService;
        this.psmProxy = psmProxy;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.webClient = webClient;
        this.taskProcessor = taskProcessor;
    }

    public Task createTask(Task task) throws TaskServiceException {
        logger.info("Received task with global id %s".formatted(task.getGlobalTaskUUID()));

        // TODO: validation of Task
        // check if applicationUUID does exist
        // check if applicationComponentId does exist

        // TODO: enrich task with additional information, name
        Optional<Node> localNode = this.nodeService.readLocalNode();
        if (localNode.isEmpty()) {
            throw new TaskServiceException("Local node not found");
        }
        task.setRemoteNodeUUID(localNode.get().getUuid().toString());
        task.setStatus(TaskStatus.NEW);
        logger.info("Created task with global ID: {} and remote task ID: {}", task.getGlobalTaskUUID(), task.getUuid());
        return this.taskRepository.save(task);
    }

    public void queueForScheduling(String taskSchedulingUuid) throws InterruptedException {
        this.taskQueue.put(taskSchedulingUuid);
    }

    public Optional<Task> readTaskById(String id) {
        return this.taskRepository.readTaskByUuid(UUID.fromString(id));
    }

    public void updateTaskInternally(String taskId, TaskStatus newStatus, float progress, String comment) throws TaskServiceException, InterruptedException {
        // check if task exists
        Optional<Task> taskOptional = this.readTaskById(taskId);
        if (taskOptional.isEmpty()) {
            throw new TaskServiceException("Task %s not found".formatted(taskId));
        }
        Task task = taskOptional.get();

        // TODO: Update task
        task.setStatus(newStatus);
        // TODO: add progress, do we need that?
        // TODO: add comment, do we need that?
        // TODO: add task to queue
        this.taskRepository.save(task);
        //this.taskQueue.put(task.getUuid().toString());
    }

    @Override
    public void reset() {
        this.taskRepository.deleteAll();
    }

    @PostConstruct
    public void init() throws TaskServiceException {
        threadPoolTaskExecutor.execute(() -> {
            logger.info("Starting task service...");
            while (isRunning.get()) {
                // TODO: start processing tasks, e.g., after restart of application, load from db
                try {
                    // process newly incoming tasks from queue
                    logger.info("TaskService is waiting for processing tasks");
                    String nextTaskId = this.taskQueue.take();

                    logger.debug("Try to read task %s from db".formatted(nextTaskId));
                    this.taskProcessor.processTask(nextTaskId);
                    // case TaskStatus.NEW:
                } catch (InterruptedException | ProxyException e) {
                    this.isRunning.set(false);
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
