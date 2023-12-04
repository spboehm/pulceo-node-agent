package dev.pulceo.pna.service;

import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NodeServiceIntegrationTests {

    @Autowired
    NodeService nodeService;

    @Test
    public void testCreateNode() {
        // given
       Node node = Node.builder()
               .pnaUUID("2f0b7383-4e5c-4392-b74c-6e85a7cfed7a")
               .name("test node")
               .nodeLocationCity("Bamberg")
               .nodeLocationCountry("Germany")
               .pnaEndpoint("http://localhost:7676")
               .host("localhost")
               .build();

        // when
        Node createdNode = this.nodeService.createNode(node);

        // then
        assertEquals(node, createdNode);
    }

    @Test
    public void testIfLocalNodeIsCreatedAfterStartup() {
        // given
        // local node is created with @PostConstruct in NodeService

        // when
        Optional<Node> localNode = this.nodeService.readLocalNode();

        // then
        assert(localNode.isPresent());
        assert (localNode.get().isLocalNode());

    }

    // TODO: add read node

    @Test
    public void testReadNode() {
        // given

        // when

        // then

    }


}
