package dev.pulceo.pna.dto.task;

import dev.pulceo.pna.model.task.Task;
import dev.pulceo.pna.model.task.TaskStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class CreateNewTaskOnPnaResponseDTO {

    private String globalTaskUUID;
    private String remoteNodeUUID;
    private String remoteTaskUUID;
    private TaskStatus status;

    public static CreateNewTaskOnPnaResponseDTO fromTask(Task task) {
        return CreateNewTaskOnPnaResponseDTO.builder()
                .globalTaskUUID(task.getGlobalTaskUUID())
                .remoteNodeUUID(task.getRemoteNodeUUID())
                .remoteTaskUUID(task.getUuid().toString())
                .status(task.getStatus())
                .build();
    }

}
