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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@Service
public class TaskService {

    private final Logger logger = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final NodeService nodeService;
    // TODO: may configure as bean?
    private final BlockingQueue<String> taskQueue = new ArrayBlockingQueue<>(1000);
    private final PSMProxy psmProxy;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Autowired
    public TaskService(TaskRepository taskRepository, NodeService nodeService, PSMProxy psmProxy, ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.taskRepository = taskRepository;
        this.nodeService = nodeService;
        this.psmProxy = psmProxy;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
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

    // TODO:
    @PostConstruct
    private void init() throws TaskServiceException {
        threadPoolTaskExecutor.execute(() -> {
            // TODO: start processing tasks, e.g., after restart of application
            // process newly incoming tasks from queue
            try {
                this.processTasks();
            } catch (InterruptedException | ProxyException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void processTasks() throws InterruptedException, ProxyException {
        // TODO: to this in bg thread

        // get task from queue
        logger.info("TaskService is waiting for processing tasks");
        String nextTaskId = this.taskQueue.take();

        logger.debug("Try to read task %s from db".formatted(nextTaskId));
        Optional<Task> optionalOfTaskToBeProcessed = this.readTaskById(nextTaskId);
        if (optionalOfTaskToBeProcessed.isEmpty()) {
            throw new ProxyException("Task %s not found".formatted(nextTaskId));
        }
        Task taskToBeUpdated = optionalOfTaskToBeProcessed.get();

        // TODO: check if application exists
        logger.debug("Simulate checking if application exists");
        Thread.sleep(1000);

        // TODO: check if application component exists

        // TODO: check if endpoint is available

        // TODO: pass task to application component and change status to RUNNING
        taskToBeUpdated.setStatus(TaskStatus.RUNNING);
        this.logger.debug("Set task %s to status %s.".formatted(taskToBeUpdated.getUuid(), taskToBeUpdated.getStatus()));
        this.taskRepository.save(taskToBeUpdated);

        // TODO: then propagate status change to psm via PSM Proxy
        this.psmProxy.updateTask(taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
        this.logger.debug("Update task %s by using PSMProxy");
    }


}
