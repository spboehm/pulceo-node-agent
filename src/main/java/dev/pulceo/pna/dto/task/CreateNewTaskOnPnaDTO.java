package dev.pulceo.pna.dto.task;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class CreateNewTaskOnPnaDTO {
    private String applicationUUID; // local application UUID on device (remote from psm)
    private String applicationComponentId; // local application component id on device (remote from psm)
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
}
