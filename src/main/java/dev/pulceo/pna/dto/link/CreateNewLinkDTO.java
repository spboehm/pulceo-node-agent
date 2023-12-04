package dev.pulceo.pna.dto.link;

import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.link.LinkDirectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewLinkDTO {
    private String name;
    private ResourceType resourceType;
    @Builder.Default
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private UUID srcNodeUUID;
    private UUID destNodeUUID;
}
