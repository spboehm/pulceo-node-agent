package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class LinkServiceIntegrationTests {

    @Autowired
    NodeService nodeService;

    @Autowired
    LinkService linkService;

    @Autowired
    JobService jobService;

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
        List<Link> listOfAllLinks = this.linkService.readAllLinks();

        // then
        assertTrue(listOfAllLinks.contains(testLink1));
        assertTrue(listOfAllLinks.contains(testLink2));
    }

    @Test
    public void testReadLinkByDestNodeWithExistingDestNode() throws LinkServiceException {
        // given
        long firstSrcNodeId = nodeService.createNode(new Node("testSrcNode1", "Germany", "Bamberg"));
        long firstDestNodeId = nodeService.createNode(new Node("testDestNode1", "Germany", "Bamberg"));
        Link testLink1 = new Link("testLink", ResourceType.NODE, firstSrcNodeId, firstDestNodeId);
        this.linkService.createLink(testLink1);

        // when
        Optional<Link> link = this.linkService.readLinkByDestNode(firstDestNodeId);


        // then
        assertTrue(link.isPresent());
        assertEquals(testLink1, link.get());
    }

    @Test
    public void testAddPingJobJobToExistingLink() throws LinkServiceException, JobServiceException {
        // given
        long firstSrcNodeId = nodeService.createNode(new Node("testSrcNode1", "Germany", "Bamberg"));
        long firstDestNodeId = nodeService.createNode(new Node("testDestNode1", "Germany", "Bamberg"));
        Link testLink1 = new Link("testLink", ResourceType.NODE, firstSrcNodeId, firstDestNodeId);
        long linkId = this.linkService.createLink(testLink1);

        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 15);
        long pingJobId = this.jobService.createPingJob(pingJob);

        // when
        this.linkService.addJobToLink(linkId, pingJobId);

        // then
        Optional<Link> updatedLink = this.linkService.readLink(linkId);
        assertEquals(pingJobId, updatedLink.get().getPingJob().getId());

    }

    // TODO: add NpingJob
    @Test
    public void testAddNpingJobToExistingLink() {


    }

    // TODO: add NpingJob
    @Test
    public void testAddIperfJobToExistingLink() {


    }

}
