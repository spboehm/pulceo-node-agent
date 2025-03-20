package dev.pulceo.pna.proxy.dto;

import dev.pulceo.pna.model.task.TaskStatus;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class UpdateTaskFromPnaDTO {

    private String globalTaskUUID; // based on the one from PSM
    private String remoteTaskUUID;
    private TaskStatus newTaskStatus;
    private String modifiedByRemoteNodeUUID; // always the pna remote node uuid
    private Timestamp modifiedOn = Timestamp.valueOf(LocalDateTime.now()); // timestamp where task is modified on device

}
