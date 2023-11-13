package dev.pulceo.pna;

import dev.pulceo.pna.model.node.Node;

public class NodeUtil {

    public static Node createTestSrcNode() {
        return Node.builder().
                name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .endpoint("http://localhost:7676")
                .build();
    }

    public static Node createTestDestNode() {
        return Node.builder().
                name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .endpoint("http://localhost:7676")
                .build();
    }

    public static Node createTestSrcNode2() {
        return Node.builder()
                .name("testSrcNode2")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Erlangen")
                .endpoint("http://localhost:7676")
                .build();
    }

    public static Node createTestDestNod2() {
        return Node.builder()
                .name("testDestNode2")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Erlangen")
                .endpoint("http://localhost:7676")
                .build();
    }

    public static Node createTestSrcNodeWithId(Long id) {
        return Node.builder()
                .id(id)
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .endpoint("http://localhost:7676")
                .build();
    }

    public static Node createTestDestNodeWithId(Long id) {
        return Node.builder()
                .id(id)
                .name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .endpoint("http://localhost:7676")
                .build();
    }

}
