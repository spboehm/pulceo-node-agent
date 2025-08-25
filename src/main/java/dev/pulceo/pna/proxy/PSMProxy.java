package dev.pulceo.pna.proxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.exception.ProxyException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.message.Operation;
import dev.pulceo.pna.model.message.ResourceMessage;
import dev.pulceo.pna.model.task.TaskStatus;
import dev.pulceo.pna.proxy.dto.UpdateTaskFromPnaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PSMProxy {

    // TODO: mqtt backend, mostly event-driven,  usage of HTTP API not intended
    @Value("${pna.uuid}")
    private String deviceId;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final PublishSubscribeChannel proxyMessageChannel;

    @Autowired
    public PSMProxy(PublishSubscribeChannel proxyMessageChannel) {
        this.proxyMessageChannel = proxyMessageChannel;
    }

    public void updateTask(String globaltaskId, String taskId, TaskStatus newTaskStatus, String modifiedBy) throws ProxyException {
        UpdateTaskFromPnaDTO updateTaskFromPnaDTO = UpdateTaskFromPnaDTO.builder()
                .globalTaskUUID(globaltaskId)
                .remoteTaskUUID(taskId)
                .newTaskStatus(newTaskStatus)
                .modifiedByRemoteNodeUUID(modifiedBy)
                // modified on is set by default
                .build();

        try {
            ResourceMessage resourceMessage = ResourceMessage.builder()
                    .sentBydeviceId(deviceId) // always pna, no other choice
                    .resourceType(ResourceType.TASK)
                    .operation(Operation.UPDATE)
                    .payload(objectMapper.writeValueAsString(updateTaskFromPnaDTO))
                    .build();
            this.proxyMessageChannel.send(new GenericMessage<>(resourceMessage, new MessageHeaders(Map.of("mqtt_topic", "cmd/" + deviceId + "/tasks"))));
        } catch (JsonProcessingException e) {
            throw new ProxyException(e);
        }

    }


}
