package dev.pulceo.pna.service;

import dev.pulceo.pna.NodeUtil;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

// Override default application.properties, otherwise port collision might occur
// TODO: replace with prop configuration or task exclusion
@SpringBootTest(properties = { "pna.delay.tcp.port=5002", "pna.delay.udp.port=5003", "pna.mqtt.client.id=550e8400-e29b-11d4-a716-446655440000"})
public class LinkServiceUnitTests {

    @MockBean
    NodeService nodeService;

    @Autowired
    @InjectMocks
    LinkService linkService;

    @Test
    public void testCreateLinkWithNotExistingSrcNode() throws LinkServiceException {
        // given
        Node srcNode = NodeUtil.createTestSrcNodeWithId(1L);
        Node destNode = NodeUtil.createTestDestNodeWithId(2L);
        Link link = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        when(nodeService.readNode(srcNode.getId())).thenReturn(Optional.empty());

        // when
        LinkServiceException linkServiceException = assertThrows(LinkServiceException.class, () -> {
            long id = this.linkService.createLink(link);;
        });

        // then
        assertEquals("Source node with id 1 does not exist!", linkServiceException.getMessage());
    }

    @Test
    public void testCreateLinkWithNotExistingDestNode() throws LinkServiceException {
        // given
        Node srcNode = NodeUtil.createTestSrcNodeWithId(1L);
        Node destNode = NodeUtil.createTestDestNodeWithId(2L);
        System.out.println(destNode.getId());
        Link link = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        when(nodeService.readNode(srcNode.getId())).thenReturn(Optional.of(srcNode));
        when(nodeService.readNode(destNode.getId())).thenReturn(Optional.empty());

        // when
        LinkServiceException linkServiceException = assertThrows(LinkServiceException.class, () -> {
            long id = this.linkService.createLink(link);;
        });

        // then
        assertEquals("Destination node with id 2 does not exist!", linkServiceException.getMessage());
    }

    @Test
    public void testReadLinkByDestNodeWithNotExistingNode() {
        // given
        Node destNode = NodeUtil.createTestDestNodeWithId(1L);
        when(nodeService.readNode(destNode.getId())).thenReturn(Optional.empty());

        // when
        Optional<Link> link = this.linkService.readLinkByDestNode(destNode);

        // then
        assertTrue(link.isEmpty());
    }

}
