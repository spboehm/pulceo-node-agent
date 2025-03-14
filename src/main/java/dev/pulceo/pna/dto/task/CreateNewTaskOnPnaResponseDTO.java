package dev.pulceo.pna.dto.task;

import dev.pulceo.pna.model.task.TaskStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class CreateNewTaskOnPnaResponseDTO {

    private UUID remoteNodeUUID;
    private UUID remoteTaskUUID;
    private TaskStatus status;

}
