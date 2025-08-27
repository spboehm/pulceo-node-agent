package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ProxyException;
import dev.pulceo.pna.exception.TaskServiceException;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.proxy.PSMProxy;
import dev.pulceo.pna.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Order(7)
@Service
public class TaskService implements ManagedService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final NodeService nodeService;
    private final BlockingQueue<Task> taskProcessingQueue = new LinkedBlockingQueue<>(1000);
    private final PSMProxy psmProxy;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final WebClient webClient;
    private final TaskProcessor taskProcessor;
    private final AtomicInteger taskCounterCreated = new AtomicInteger(0);
    private final AtomicInteger taskCounterUpdated = new AtomicInteger(0);

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
        logger.debug("Created tasks with status NEW: {}", taskCounterCreated.incrementAndGet());
        return this.taskRepository.save(task);
    }

    public void queueForScheduling(Task task) throws InterruptedException {
        this.taskProcessingQueue.put(task);
    }

    public Optional<Task> readTaskById(String id) {
        return this.taskRepository.readTaskByUuid(UUID.fromString(id));
    }

    @Transactional
    public Task updateTaskInternally(String taskId, TaskStatus newStatus, float progress, String comment) throws TaskServiceException, InterruptedException {
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
        logger.debug("Updated task number: {}", taskCounterUpdated.incrementAndGet());
        return this.taskRepository.save(task);
    }

    @Override
    public void reset() {
        this.taskRepository.deleteAll();
        this.taskProcessor.getTaskCounterNew().set(0);
        this.taskProcessor.getTaskCounterRunning().set(0);
        this.taskProcessor.getTaskCounterCompleted().set(0);
    }

    @PostConstruct
    public void init() throws TaskServiceException {
        threadPoolTaskExecutor.execute(() -> {
            logger.info("Starting task service...");
            while (isRunning.get()) {
                try {
                    Task task = this.taskProcessingQueue.take();
                    threadPoolTaskExecutor.execute(() -> {
                        try {
                            this.taskProcessor.processTask(task);
                        } catch (ProxyException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (InterruptedException e) {
                    this.isRunning.set(false);
                    Thread.currentThread().interrupt();
                    logger.warn("Task service interrupted, shutting down...");
                }
            }
        });
    }

}
