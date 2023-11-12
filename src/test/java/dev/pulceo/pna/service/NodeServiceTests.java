package dev.pulceo.pna.service;

import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NodeServiceTests {

    @Autowired
    NodeService nodeService;

    @Test
    public void testCreateNode() {
        // given
       Node node = Node.builder()
               .name("test node")
               .nodeLocationCity("Bamberg")
               .nodeLocationCountry("Germany")
               .build();

        // when
        long id = this.nodeService.createNode(node);

        // then
        assertTrue(id > 0);
    }

    // TODO: add read node

    @Test
    public void testReadNode() {
        // given

        // when

        // then

    }


}
