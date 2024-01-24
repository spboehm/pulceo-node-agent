package dev.pulceo.pna.dto.link;

import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.LinkJob;
import dev.pulceo.pna.model.link.LinkDirectionType;
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
    @Builder.Default
    private ResourceType resourceType = ResourceType.NODE;
    @Builder.Default
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private UUID srcNode;
    private UUID destNode;
    @Builder.Default
    private List<LinkJob> linkJobs = new ArrayList<>();
}
