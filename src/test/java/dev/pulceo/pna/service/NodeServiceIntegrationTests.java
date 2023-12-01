package dev.pulceo.pna.service;

import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NodeServiceIntegrationTests {

    @Autowired
    NodeService nodeService;

    @Value("${pna.id}")
    private String pnaId;

    @Test
    public void testCreateNode() {
        // given
       Node node = Node.builder()
               .pnaId(pnaId)
               .name("test node")
               .nodeLocationCity("Bamberg")
               .nodeLocationCountry("Germany")
               .endpoint("http://localhost:7676")
               .build();

        // when
        Node createdNode = this.nodeService.createNode(node);

        // then
        assertEquals(node, createdNode);
    }

    // TODO: add read node

    @Test
    public void testReadNode() {
        // given

        // when

        // then

    }


}
