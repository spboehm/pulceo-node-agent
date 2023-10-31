package dev.pulceo.pna.service;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LinkServiceUnitTests {

    @MockBean
    NodeService nodeService;

    @Autowired
    @InjectMocks
    LinkService linkService;

    @Test
    public void testCreateLinkWithNotExistingSrcNode() throws LinkServiceException {
        // given
        long srcNodeId = 1;
        long destNodeId = 2;
        Link link = new Link("testLink", ResourceType.NODE, srcNodeId, destNodeId);
        when(nodeService.readNode(srcNodeId)).thenReturn(Optional.empty());

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
        long srcNodeId = 1;
        long destNodeId = 2;
        Link link = new Link("testLink", ResourceType.NODE, srcNodeId, destNodeId);
        when(nodeService.readNode(srcNodeId)).thenReturn(Optional.of(new Node()));
        when(nodeService.readNode(destNodeId)).thenReturn(Optional.empty());

        // when
        LinkServiceException linkServiceException = assertThrows(LinkServiceException.class, () -> {
            long id = this.linkService.createLink(link);;
        });

        // then
        assertEquals("Destination node with id 2 does not exist!", linkServiceException.getMessage());
    }

}
