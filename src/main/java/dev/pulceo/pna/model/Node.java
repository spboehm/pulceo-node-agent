package dev.pulceo.pna.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "UUID is required!")
    private final String uuid = UUID.randomUUID().toString();
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
    @NotEmpty(message ="Node location longitude is required!")
    private double NodeLocationLongitude = 0.000000;
    @NotEmpty(message="Node location latitude is required!")
    private double NodeLocationLatitude = 0.000000;

    public Node(String name, NodeType type) {
        this.name = name;
        this.type = type;
    }
}
