package dev.pulceo.pna.service;

import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LinkServiceTests {

    @Autowired
    NodeService nodeService;

    @Autowired
    LinkService linkService;

    @Test
    public void testCreateLink() {
        // given
        long srcNodeId = nodeService.createNode(new Node("testSrcNode", "Germany", "Bamberg"));
        long destNodeId = nodeService.createNode(new Node("testDestNode", "Germany", "Bamberg"));
        Link link = new Link("testLink", srcNodeId, destNodeId);

        // when
        long id = this.linkService.createLink(link);

        // then
        assertTrue(id > 0);

    }


}
