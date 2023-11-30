package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.LinkRepository;
import dev.pulceo.pna.util.NodeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LinkServiceUnitTests {

    @Mock
    NodeService nodeService;

    @Mock
    LinkRepository linkRepository;

    @InjectMocks
    LinkService linkService;

    private final String pnaId = "0247fea1-3ca3-401b-8fa2-b6f83a469680";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(nodeService, "pnaId", pnaId);
    }

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

        // when
        Optional<Link> link = this.linkService.readLinkByDestNode(destNode);

        // then
        assertTrue(link.isEmpty());
    }

}
