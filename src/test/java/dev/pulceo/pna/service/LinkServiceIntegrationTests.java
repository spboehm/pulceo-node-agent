package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LinkServiceIntegrationTests {

    @Autowired
    NodeService nodeService;

    @Autowired
    LinkService linkService;

    @Test
    public void testCreateLinkWithExistingNodes() throws LinkServiceException {
        // given
        long srcNodeId = nodeService.createNode(new Node("testSrcNode", "Germany", "Bamberg"));
        long destNodeId = nodeService.createNode(new Node("testDestNode", "Germany", "Bamberg"));
        Link link = new Link("testLink", ResourceType.NODE, srcNodeId, destNodeId);

        // when
        long id = this.linkService.createLink(link);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testReadLink() throws LinkServiceException {
        // given
        long srcNodeId = nodeService.createNode(new Node("testSrcNode", "Germany", "Bamberg"));
        long destNodeId = nodeService.createNode(new Node("testDestNode", "Germany", "Bamberg"));
        Link expectedLink = new Link("testLink", ResourceType.NODE, srcNodeId, destNodeId);
        long createdLinkId = this.linkService.createLink(expectedLink);

        // when
        Optional<Link> optionalOfReadLink = this.linkService.readLink(createdLinkId);

        // then
        assertTrue(optionalOfReadLink.isPresent());
        assertEquals(expectedLink, optionalOfReadLink.get());
    }

    @Test
    public void testReadAllLinks() throws LinkServiceException {
        // given
        long firstSrcNodeId = nodeService.createNode(new Node("testSrcNode1", "Germany", "Bamberg"));
        long firstDestNodeId = nodeService.createNode(new Node("testDestNode1", "Germany", "Bamberg"));
        Link testLink1 = new Link("testLink", ResourceType.NODE, firstSrcNodeId, firstDestNodeId);
        this.linkService.createLink(testLink1);
        long secondSrcNodeId = nodeService.createNode(new Node("testSrcNode2", "Germany", "Erlangen"));
        long secondDestNodeId = nodeService.createNode(new Node("testDestNode2", "Germany", "Erlangen"));
        Link testLink2 = new Link("testLink2", ResourceType.NODE, secondSrcNodeId, secondDestNodeId);
        this.linkService.createLink(testLink2);

        // when


        // then

    }
}
