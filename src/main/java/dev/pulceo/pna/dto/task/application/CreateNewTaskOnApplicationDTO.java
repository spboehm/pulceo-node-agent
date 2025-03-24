package dev.pulceo.pna.dto.task.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class CreateNewTaskOnApplicationDTO {

    @Builder.Default
    @JsonProperty("uuid")
    private String remoteTaskUUID = ""; // the one from PNA, internal
    @Builder.Default
    private byte[] payload = new byte[0]; // payload of the task
    @Builder.Default
    private String callbackProtocol = ""; // statically generated, never changed
    @Builder.Default
    private String callbackEndpoint = ""; // statically generated, never changed


}
