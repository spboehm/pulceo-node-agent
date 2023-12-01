package dev.pulceo.pna.model.node;

import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.jobs.NodeJob;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"nodeJobs"})
public class Node extends Resource {

    @NotBlank(message= "PNA id is required!")
    private String pnaId;

    @Builder.Default
    @NotNull
    public boolean isLocalNode = false;

    @NotBlank(message="Name is required!")
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

    @Builder.Default
    @NotNull
    private String nodeLocationCountry = "";

    @Builder.Default
    @NotNull
    private String nodeLocationCity = "";

    @Builder.Default
    @Min(-180)
    @Max(180)
    private double nodeLocationLongitude = 0.000000;

    @Builder.Default
    @Min(-90)
    @Max(90)
    private double nodeLocationLatitude = 0.000000;

    @NotBlank(message="Node endpoint is required!")
    @URL
    private String endpoint;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "node", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NodeJob> nodeJobs = new ArrayList<>();

}
