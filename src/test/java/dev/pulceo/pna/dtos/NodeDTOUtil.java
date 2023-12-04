package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.node.CreateNewNodeDTO;

public class NodeDTOUtil {

    public static CreateNewNodeDTO createTestSrcNode() {
        return CreateNewNodeDTO.builder()
                .pnaUUID("0247fea1-3ca3-401b-8fa2-b6f83a469680")
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

}
