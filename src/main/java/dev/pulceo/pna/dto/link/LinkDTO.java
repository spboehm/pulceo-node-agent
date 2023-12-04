package dev.pulceo.pna.dto.link;

import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.LinkJob;
import dev.pulceo.pna.model.link.LinkDirectionType;
import dev.pulceo.pna.model.node.Node;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkDTO {
    private UUID linkUUID;
    private String name;
    private ResourceType resourceType;
    @Builder.Default
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private Node srcNode;
    private Node destNode;
    @Builder.Default
    private List<LinkJob> linkJobs = new ArrayList<>();
}