package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Node extends Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message= "Name is required!")
    private String name;
    @NotNull(message = "Node type is required!")
    private NodeType type;
    @Min(1)
    @Max(16)
    private int layer = 1;
    @NotNull(message="Node role is required!")
    private NodeRole role = NodeRole.WORKLOAD;
    @NotEmpty(message="Node location city is required!")
    private String NodeLocationCity = "";
    @NotEmpty(message="Node location country is required!")
    private String NodeLocationCountry = "";
    private double NodeLocationLongitude = 0.000000;
    private double NodeLocationLatitude = 0.000000;

}
