package dev.pulceo.pna.service;

import dev.pulceo.pna.dto.task.application.CreateNewTaskOnApplicationDTO;
import dev.pulceo.pna.exception.ProxyException;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.proxy.PSMProxy;
import dev.pulceo.pna.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;

@Component
public class TaskProcessor {

    private final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private final TaskRepository taskRepository;
    private final PSMProxy psmProxy;
    private final WebClient webClient;

    public TaskProcessor(TaskRepository taskRepository, PSMProxy psmProxy, WebClient webClient) {
        this.taskRepository = taskRepository;
        this.psmProxy = psmProxy;
        this.webClient = webClient;
    }

    @Transactional
    public void processTask(String nextTaskId) throws ProxyException {
        Optional<Task> optionalOfTaskToBeProcessed = this.taskRepository.readTaskByUuid(UUID.fromString(nextTaskId));
        if (optionalOfTaskToBeProcessed.isEmpty()) {
            throw new ProxyException("Task %s not found".formatted(nextTaskId));
        }
        Task taskToBeUpdated = optionalOfTaskToBeProcessed.get();

        if (taskToBeUpdated.getStatus() == TaskStatus.NEW) {
            // case TaskStatus.NEW:
            this.processNewTask(taskToBeUpdated);
        } else if (taskToBeUpdated.getStatus() == TaskStatus.RUNNING) {
            // TODO: case TaskStatus.RUNNING:update progress for example ???
            this.processRunningTask(nextTaskId, taskToBeUpdated);
        } else if (taskToBeUpdated.getStatus() == TaskStatus.COMPLETED) {
            // case TaskStatus.COMPLETED:
            this.processFinishedTask(taskToBeUpdated);
        } else {
            logger.warn("Unhandled task status: {}", taskToBeUpdated.getStatus());
        }
    }

    @Transactional
    public void processNewTask(Task taskToBeUpdated) throws ProxyException {
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

        // TODO: then propagate status change to psm via PSM Proxy
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
    }

    @Transactional
    public void processRunningTask(String nextTaskId, Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process running task %s".formatted(nextTaskId));
        // TODO: check if application exists

        // TODO: check if application component exists

        // TODO: check if endpoint is available

        // TODO: pass task to application component and change status to RUNNING
        // TODO: additional check from application is needed, e.g., if task is really running

        // TODO: update running task with progress
        // TODO: then propagate status change to psm via PSM Proxy
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
    }

    @Transactional
    public void processFinishedTask(Task taskToBeUpdated) throws ProxyException {
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
    }

}
