package dev.pulceo.pna;

import dev.pulceo.pna.model.node.Node;

public class Util {

    public static Node createTestSrcNode() {
        return Node.builder().
                name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .build();
    }

    public static Node createTestDestNode() {
        return Node.builder().
                name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .build();
    }

    public static Node createTestSrcNodeWithId(Long id) {
        return Node.builder()
                .id(id)
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .build();
    }

    public static Node createTestDestNodeWithId(Long id) {
        return Node.builder()
                .id(id)
                .name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .build();
    }
}
