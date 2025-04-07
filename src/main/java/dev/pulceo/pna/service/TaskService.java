package dev.pulceo.pna.service;

import dev.pulceo.pna.dto.task.application.CreateNewTaskOnApplicationDTO;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final NodeService nodeService;
    // TODO: may configure as bean?
    private final BlockingQueue<String> taskQueue = new ArrayBlockingQueue<>(1000);
    private final PSMProxy psmProxy;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private final WebClient webClient;


    @Autowired
    public TaskService(TaskRepository taskRepository, NodeService nodeService, PSMProxy psmProxy, ThreadPoolTaskExecutor threadPoolTaskExecutor, WebClient webClient) {
        this.taskRepository = taskRepository;
        this.nodeService = nodeService;
        this.psmProxy = psmProxy;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
        this.webClient = webClient;
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

        Task persistedTask = this.taskRepository.save(task);

        // TODO: enqueue task
        try {
            this.taskQueue.put(persistedTask.getUuid().toString());
            logger.info("Task created: " + task + " , remaining capacity is: " + taskQueue.remainingCapacity());
        } catch (InterruptedException e) {
            throw new TaskServiceException("Task queue is full");
        }
        return persistedTask;
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
        this.taskQueue.put(task.getUuid().toString());
    }

    @PostConstruct
    private void init() throws TaskServiceException {
        threadPoolTaskExecutor.execute(() -> {
            logger.info("Starting task service...");
            while (isRunning.get()) {
                // TODO: start processing tasks, e.g., after restart of application, load from db
                try {
                    // process newly incoming tasks from queue
                    logger.info("TaskService is waiting for processing tasks");
                    String nextTaskId = this.taskQueue.take();

                    logger.debug("Try to read task %s from db".formatted(nextTaskId));
                    Optional<Task> optionalOfTaskToBeProcessed = this.readTaskById(nextTaskId);
                    if (optionalOfTaskToBeProcessed.isEmpty()) {
                        throw new ProxyException("Task %s not found".formatted(nextTaskId));
                    }
                    Task taskToBeUpdated = optionalOfTaskToBeProcessed.get();

                    // TODO: differentiate between NEW and RUNNING tasks
                    // TODO: CREATE and UPDATE
                    if (taskToBeUpdated.getStatus() == TaskStatus.NEW) {
                        // case TaskStatus.NEW:
                        processNewTask(taskToBeUpdated);
                    } else if (taskToBeUpdated.getStatus() == TaskStatus.RUNNING) {
                        // TODO: case TaskStatus.RUNNING:update progress for example ???
                        processRunningTask(nextTaskId, taskToBeUpdated);
                    } else if (taskToBeUpdated.getStatus() == TaskStatus.COMPLETED) {
                        // case TaskStatus.COMPLETED:
                        processFinishedTask(taskToBeUpdated);
                    }
                    // case TaskStatus.NEW:
                } catch (InterruptedException | ProxyException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // TODO: this is just a copy, try to refactor
    private void processRunningTask(String nextTaskId, Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process running task %s".formatted(nextTaskId));
        // TODO: check if application exists

        // TODO: check if application component exists

        // TODO: check if endpoint is available

        // TODO: pass task to application component and change status to RUNNING
        // TODO: additional check from application is needed, e.g., if task is really running

        taskToBeUpdated.setStatus(TaskStatus.RUNNING);
        // TODO: update running task with progress
        this.taskRepository.save(taskToBeUpdated);
        this.logger.debug("Set task %s to status %s.".formatted(taskToBeUpdated.getUuid(), taskToBeUpdated.getStatus()));
        // TODO: then propagate status change to psm via PSM Proxy
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
        this.logger.debug("Update task %s by using PSMProxy");
    }


    private void processNewTask(Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process new task %s".formatted(taskToBeUpdated.getUuid()));
        // TODO: check if application exists

        // TODO: check if application component exists

        // TODO: check if endpoint is available

        // TODO: pass task to application component and change status to RUNNING
        // TODO: additional check from application is needed, e.g., if task is really running

        /* pass to application component */
        // TODO: replace dynamically with HTTP / MQTT
        CreateNewTaskOnApplicationDTO createNewTaskOnApplicationDTO = CreateNewTaskOnApplicationDTO.builder()
                .globalTaskUUID(taskToBeUpdated.getGlobalTaskUUID())
                .remoteTaskUUID(taskToBeUpdated.getUuid().toString())
                .payload(taskToBeUpdated.getPayload())
                .callbackProtocol(taskToBeUpdated.getCallbackProtocol())
                .callbackEndpoint(taskToBeUpdated.getCallbackEndpoint())
                .build();

        // this is equals the Kubernetes service name
        String applicationComponentId = taskToBeUpdated.getApplicationComponentId();

        webClient.post()
                .uri("https://" + applicationComponentId + "/tasks")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .bodyValue(createNewTaskOnApplicationDTO)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorComplete(e -> {
                    logger.error("Task assignment failed with error: %s".formatted(e.getMessage()));
                    return false;
                })
                .block();

        taskToBeUpdated.setStatus(TaskStatus.RUNNING);
        this.logger.debug("Set task %s to status %s.".formatted(taskToBeUpdated.getUuid(), taskToBeUpdated.getStatus()));
        this.taskRepository.save(taskToBeUpdated);

        // TODO: then propagate status change to psm via PSM Proxy
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
        this.logger.debug("Update task %s by using PSMProxy");
    }

    private void processFinishedTask(Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process finished task %s".formatted(taskToBeUpdated.getUuid()));
        taskToBeUpdated.setStatus(TaskStatus.COMPLETED);
        this.logger.debug("Set task %s to status %s.".formatted(taskToBeUpdated.getUuid(), taskToBeUpdated.getStatus()));
        this.taskRepository.save(taskToBeUpdated);

        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
        this.logger.debug("Update task %s by using PSMProxy");
    }


}
