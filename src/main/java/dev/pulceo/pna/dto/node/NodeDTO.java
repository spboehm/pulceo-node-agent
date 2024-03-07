package dev.pulceo.pna.dto.node;

import dev.pulceo.pna.model.jobs.NodeJob;
import dev.pulceo.pna.model.node.NodeRole;
import dev.pulceo.pna.model.node.NodeType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NodeDTO {

    @NotBlank(message="UUID is required!")
    private UUID nodeUUID;

    @NotBlank(message= "PNA id is required!")
    private String pnaUUID;

    @Builder.Default
    private boolean isLocalNode = false;

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
    @Column(name = "node_group")
    private String group = "";

    @Builder.Default
    @NotNull
    private String country = "";

    @Builder.Default
    @NotNull
    private String city = "";

    @Builder.Default
    @Min(-180)
    @Max(180)
    private double longitude = 0.000000;

    @Builder.Default
    @Min(-90)
    @Max(90)
    private double latitude = 0.000000;

    @NotBlank(message="Node endpoint is required!")
    @URL
    private String pnaEndpoint;

    @NotBlank(message="Node hostname is required!")
    private String host;

    @Builder.Default
    private List<NodeJob> nodeJobs = new ArrayList<>();

}
