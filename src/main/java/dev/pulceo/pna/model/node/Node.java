package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Node extends Resource {

    @NotBlank(message= "Name is required!")
    private String name;

    @NotNull(message = "Node type is required!")
    private NodeType type;

    @Builder.Default
    @Min(1)
    @Max(16)
    private int layer = 1;

    @Builder.Default
    @NotNull(message="Node role is required!")
    private NodeRole role = NodeRole.WORKLOAD;

    @NotEmpty(message="Node location city is required!")
    private String NodeLocationCity;

    @NotEmpty(message="Node location country is required!")
    private String NodeLocationCountry;

    private double NodeLocationLongitude;
    private double NodeLocationLatitude;

}
