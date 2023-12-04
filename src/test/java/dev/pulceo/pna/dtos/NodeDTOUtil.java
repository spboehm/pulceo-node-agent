package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.node.CreateNewNodeDTO;

public class NodeDTOUtil {

    public static CreateNewNodeDTO createTestDestNode() {
        return CreateNewNodeDTO.builder()
                .pnaUUID("4c961268-df2a-49c1-965a-2e5036158ac0")
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

}