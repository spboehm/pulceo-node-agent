package dev.pulceo.pna.service;

import dev.pulceo.pna.dto.task.application.CreateNewTaskOnApplicationDTO;
import dev.pulceo.pna.exception.ProxyException;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.proxy.PSMProxy;
import dev.pulceo.pna.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TaskProcessor {

    private final Logger logger = LoggerFactory.getLogger(TaskProcessor.class);

    private final TaskRepository taskRepository;
    private final PSMProxy psmProxy;
    private final WebClient webClient;
    @Getter
    private final AtomicInteger taskCounterNew = new AtomicInteger(0);
    @Getter
    private final AtomicInteger taskCounterRunning = new AtomicInteger(0);
    @Getter
    private final AtomicInteger taskCounterCompleted = new AtomicInteger(0);

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
            this.processNewTask(taskToBeUpdated);
            logger.debug("Processed tasks with status [NEW] " + taskCounterNew.incrementAndGet());
        } else if (taskToBeUpdated.getStatus() == TaskStatus.RUNNING) {
            // TODO: case TaskStatus.RUNNING:update progress for example ???
            this.processRunningTask(nextTaskId, taskToBeUpdated);
            logger.debug("Processed tasks with status [RUNNING] " + taskCounterRunning.incrementAndGet());
        } else if (taskToBeUpdated.getStatus() == TaskStatus.COMPLETED) {
            this.processCompletedTask(taskToBeUpdated);
            logger.debug("Processed tasks with status [COMPLETED] " + taskCounterCompleted.incrementAndGet());
        } else {
            logger.warn("Unhandled task status: {}", taskToBeUpdated.getStatus());
        }
    }

    @Transactional
    public void processNewTask(Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process NEW task with id %s".formatted(taskToBeUpdated.getUuid()));
        // TODO: check if application exists

        // TODO: check if application component exists

        // TODO: check if endpoint is available

        // TODO: pass task to application component and change status to RUNNING

        // TODO: it is either done by application or it is done by the PNA

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
                .doOnSuccess(aVoid -> {
                    logger.debug("Task %s has been successfully assigned to application component %s".formatted(taskToBeUpdated.getUuid(), applicationComponentId));
                    try {
                        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), TaskStatus.RUNNING, taskToBeUpdated.getRemoteNodeUUID());
                    } catch (ProxyException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onErrorResume(e -> {
                    logger.error("Task assignment failed with error: %s".formatted(e.getMessage()));
                    try {
                        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), TaskStatus.FAILED, taskToBeUpdated.getRemoteNodeUUID());
                    } catch (ProxyException ex) {
                        throw new RuntimeException(ex);
                    }
                    return Mono.empty();
                })
                .block();
    }

    @Transactional
    public void processRunningTask(String nextTaskId, Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process RUNNING task with id %s".formatted(nextTaskId));
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
    public void processCompletedTask(Task taskToBeUpdated) throws ProxyException {
        logger.debug("Process COMPLETED task %s".formatted(taskToBeUpdated.getUuid()));
        this.psmProxy.updateTask(taskToBeUpdated.getGlobalTaskUUID(), taskToBeUpdated.getUuid().toString(), taskToBeUpdated.getStatus(), taskToBeUpdated.getRemoteNodeUUID());
    }

}
