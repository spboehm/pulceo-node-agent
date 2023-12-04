package dev.pulceo.pna.util;

import dev.pulceo.pna.model.node.Node;

public class NodeUtil {

    public static Node createTestSrcNode() {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469680")
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

    public static Node createTestDestNode() {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469681")
                .name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

    public static Node createTestSrcNode2() {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469682")
                .name("testSrcNode2")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Erlangen")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

    public static Node createTestDestNode2() {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469683")
                .name("testDestNode2")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Erlangen")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

    // TODO: consider replacing wit proper unit tests
    public static Node createTestSrcNodeWithId(Long id) {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469684")
                .id(id)
                .name("testSrcNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

    // TODO: consider replacing wit proper unit tests
    public static Node createTestDestNodeWithId(Long id) {
        return Node.builder()
                .pnaId("0247fea1-3ca3-401b-8fa2-b6f83a469685")
                .id(id)
                .name("testDestNode")
                .nodeLocationCountry("Germany")
                .nodeLocationCity("Bamberg")
                .pnaEndpoint("http://localhost:7676")
                .host("localhost")
                .build();
    }

}
