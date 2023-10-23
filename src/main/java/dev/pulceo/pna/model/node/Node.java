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

    @NotNull(message = "Node type is required!")
    private NodeType type;

    @Min(1)
    @Max(16)
    private int layer;

    @NotNull(message="Node role is required!")
    private NodeRole role;

    @NotBlank(message="Node location country is required!")
    private String nodeLocationCountry;

    @NotBlank(message="Node location city is required!")
    private String nodeLocationCity;

    @Min(-180)
    @Max(180)
    private double nodeLocationLongitude;

    @Min(-90)
    @Max(90)
    private double nodeLocationLatitude;

    public Node(String name, String nodeLocationCountry, String nodeLocationCity) {
        this.name = name;
        this.type = NodeType.EDGE;
        this.layer = 1;
        this.role = NodeRole.WORKLOAD;
        this.nodeLocationCountry = nodeLocationCountry;
        this.nodeLocationCity = nodeLocationCity;
        this.nodeLocationLongitude = 0.000000;
        this.nodeLocationLatitude = 0.000000;
    }

}
