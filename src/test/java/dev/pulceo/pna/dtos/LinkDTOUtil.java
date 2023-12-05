package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.link.CreateNewLinkDTO;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.link.LinkDirectionType;

import java.util.UUID;

public class LinkDTOUtil {

    public static CreateNewLinkDTO createTestLink(String srcNodeUUID, String destNodeUUID) {
        return CreateNewLinkDTO.builder()
                .name("testLink")
                .resourceType(ResourceType.NODE)
                .linkDirectionType(LinkDirectionType.UNDIRECTED)
                .srcNodeUUID(UUID.fromString(srcNodeUUID))
                .destNodeUUID(UUID.fromString(destNodeUUID))
                .build();
    }

}
