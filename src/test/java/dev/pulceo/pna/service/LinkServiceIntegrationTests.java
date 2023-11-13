package dev.pulceo.pna.service;

import dev.pulceo.pna.NodeUtil;
import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.iperf.IperfClientProtocol;
import dev.pulceo.pna.model.iperf.IperfRequest;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.model.jobs.NpingJob;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingRequest;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link link = new Link("testLink", ResourceType.NODE, srcNode, destNode);

        // when
        long id = this.linkService.createLink(link);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testReadLink() throws LinkServiceException {
        // given
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link expectedLink = new Link("testLink", ResourceType.NODE, srcNode, destNode);
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
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link testLink1 = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        this.linkService.createLink(testLink1);
        Node secondSrcNode = nodeService.createNode(NodeUtil.createTestSrcNode2());
        Node secondDestNode = nodeService.createNode(NodeUtil.createTestDestNod2());
        Link testLink2 = new Link("testLink2", ResourceType.NODE, secondSrcNode, secondDestNode);
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
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link testLink1 = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        this.linkService.createLink(testLink1);

        // when
        Optional<Link> link = this.linkService.readLinkByDestNode(destNode);


        // then
        assertTrue(link.isPresent());
        assertEquals(testLink1, link.get());
    }

    @Test
    // only the associated jobs are loaded from the db, PingRequest is not loaded
    public void testAddPingJobToExistingLink() throws LinkServiceException, JobServiceException {
        // given
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link testLink1 = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        long linkId = this.linkService.createLink(testLink1);

        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");
        PingJob pingJob = new PingJob(pingRequest, 15);
        long pingJobId = this.jobService.createPingJob(pingJob);

        // when
        this.linkService.addJobToLink(linkId, pingJobId);

        // then
        Optional<Link> updatedLink = this.linkService.readLink(linkId);
        assertTrue(updatedLink.isPresent());
        Link actualLink = updatedLink.get();
        assertEquals(testLink1.getName(), actualLink.getName());
        assertEquals(testLink1.getResourceType(), actualLink.getResourceType());
        assertEquals(testLink1.getLinkDirectionType(), actualLink.getLinkDirectionType());
        assertEquals(testLink1.getSrcNode(), actualLink.getSrcNode());
        assertEquals(testLink1.getDestNode(), actualLink.getDestNode());
        assertFalse(updatedLink.get().getJobs().isEmpty());
    }

    @Test
    // only the associated jobs are loaded from the db, PingRequest is not loaded
    public void testAddNPingJobToExistingLink() throws LinkServiceException, JobServiceException {
        // given
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link testLink1 = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        long linkId = this.linkService.createLink(testLink1);

        NpingRequest npingRequest = new NpingRequest("localhost", "localhost", 4002, NpingClientProtocol.TCP, 1, "lo");
        NpingJob npingJob = new NpingJob(npingRequest, 15);
        long npingJobId = this.jobService.createNpingJob(npingJob);

        // when
        this.linkService.addJobToLink(linkId, npingJobId);

        // then
        Optional<Link> updatedLink = this.linkService.readLink(linkId);
        assertTrue(updatedLink.isPresent());
        Link actualLink = updatedLink.get();
        assertEquals(testLink1.getName(), actualLink.getName());
        assertEquals(testLink1.getResourceType(), actualLink.getResourceType());
        assertEquals(testLink1.getLinkDirectionType(), actualLink.getLinkDirectionType());
        assertEquals(testLink1.getSrcNode(), actualLink.getSrcNode());
        assertEquals(testLink1.getDestNode(), actualLink.getDestNode());
        assertFalse(updatedLink.get().getJobs().isEmpty());
    }

    @Test
    // only the associated jobs are loaded from the db, PingRequest is not loaded
    public void testAddIperfJobToExistingLink() throws LinkServiceException, JobServiceException {
        // given
        Node srcNode = nodeService.createNode(NodeUtil.createTestSrcNode());
        Node destNode = nodeService.createNode(NodeUtil.createTestDestNode());
        Link testLink1 = new Link("testLink", ResourceType.NODE, srcNode, destNode);
        long linkId = this.linkService.createLink(testLink1);

        IperfRequest iperfRequest = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP, "lo");
        IperfJob iperfJob = new IperfJob(iperfRequest, 15);
        long iperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        this.linkService.addJobToLink(linkId, iperfJobId);

        // then
        Optional<Link> updatedLink = this.linkService.readLink(linkId);
        assertTrue(updatedLink.isPresent());
        Link actualLink = updatedLink.get();
        assertEquals(testLink1.getName(), actualLink.getName());
        assertEquals(testLink1.getResourceType(), actualLink.getResourceType());
        assertEquals(testLink1.getLinkDirectionType(), actualLink.getLinkDirectionType());
        assertEquals(testLink1.getSrcNode(), actualLink.getSrcNode());
        assertEquals(testLink1.getDestNode(), actualLink.getDestNode());
        assertFalse(updatedLink.get().getJobs().isEmpty());
    }

}
