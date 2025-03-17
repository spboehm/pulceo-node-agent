package dev.pulceo.pna.model.message;

import dev.pulceo.pna.model.ResourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@SuperBuilder
public class ResourceMessage implements Serializable {

    @Value("${device.id}")
    private String sentBydeviceId;
    private ResourceType resourceType;
    private Operation operation;
    private String payload;

}
