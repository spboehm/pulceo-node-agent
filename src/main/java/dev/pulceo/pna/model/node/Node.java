package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Node extends Resource {

    @NotBlank(message= "Name is required!")
    private String name;

    @Builder.Default
    @NotNull(message = "Node type is required!")
    private NodeType type = NodeType.EDGE;

    @Builder.Default
    @Min(1)
    @Max(16)
    private int layer = 1;

    @Builder.Default
    @NotNull(message="Node role is required!")
    private NodeRole role = NodeRole.WORKLOAD;

    @NotBlank(message="Node location country is required!")
    private String nodeLocationCountry;

    @NotBlank(message="Node location city is required!")
    private String nodeLocationCity;

    @Builder.Default
    @Min(-180)
    @Max(180)
    private double nodeLocationLongitude = 0.000000;

    @Builder.Default
    @Min(-90)
    @Max(90)
    private double nodeLocationLatitude = 0.000000;

}
