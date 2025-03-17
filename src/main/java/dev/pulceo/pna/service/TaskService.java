package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.TaskServiceException;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final NodeService nodeService;

    @Autowired
    public TaskService(TaskRepository taskRepository, NodeService nodeService) {
        this.taskRepository = taskRepository;
        this.nodeService = nodeService;
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

        // TODO: persist task
        Task persistedTask = this.taskRepository.save(task);

        /*
        *     private UUID applicationUUID; // local application UUID on device (remote from psm)
    private UUID applicationComponentId; // local application component id on device (remote from psm)
    private byte[] payload = new byte[0]; // payload of the task
    @Builder.Default
    private String callbackProtocol = ""; // statically generated, never changed
    @Builder.Default
    private String callbackEndpoint = ""; // statically generated, never changed
    @Builder.Default
    private String destinationApplicationComponentProtocol = ""; // statically generated, never changed, e.g., http
    @Builder.Default
    private String destinationApplicationComponentEndpoint = ""; // statically generated, never changed, e.g., /api/test
    @Builder.Default
    private Map<String, String> properties = new HashMap<>(); // properties of the task
        * */
        return persistedTask;

    }


}
