package dev.pulceo.pna.dto.task.internal;


import dev.pulceo.pna.model.task.TaskStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class UpdateTaskInternallyOnPNADTO {

    private String taskId;
    @Builder.Default
    private TaskStatus newTaskStatus = TaskStatus.RUNNING;
    @Builder.Default
    // TODO: do we need that?
    private float progress = 0.0f;
    @Builder.Default
    // TODO: do we need that?
    private String comment = "";

}
