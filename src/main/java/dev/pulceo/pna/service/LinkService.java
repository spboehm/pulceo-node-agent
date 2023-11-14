package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.jobs.Job;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.LinkRepository;
import dev.pulceo.pna.repository.SuperJobRespository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    SuperJobRespository jobRepository;

    @Autowired
    JobService jobService;

    @Autowired
    NodeService nodeService;

    public long createLink(Link link) throws LinkServiceException {
        if (nodeService.readNode(link.getSrcNode().getId()).isEmpty()) {
            throw new LinkServiceException("Source node with id %d does not exist!".formatted(link.getSrcNode().getId()));
        } else if (nodeService.readNode(link.getDestNode().getId()).isEmpty()) {
            throw new LinkServiceException("Destination node with id %d does not exist!".formatted(link.getDestNode().getId()));
        }
        return this.linkRepository.save(link).getId();
    }

    public Optional<Link> readLink(long id) {
        return this.linkRepository.findById(id);
    }

    public List<Link> readAllLinks() {
        return this.linkRepository.findAll();
    }

    public Optional<Link> readLinkByDestNode(Node node) { return this.linkRepository.findLinkByDestNode(node); }

    @Transactional
    public void addJobToLink(long linkId, long jobId) throws LinkServiceException, JobServiceException {
        // TODO: check if link exists AND check if job exists AND if job is not already allocated?
        Optional<Link> link = this.readLink(linkId);
        if (link.isEmpty()) {
            throw new LinkServiceException("Link with id %d does not exist!".formatted(linkId));
        }

        Optional<Job> job = this.jobService.readJob(jobId);
        if (job.isEmpty()) {
            throw new LinkServiceException("Job with id %d does not exist!".formatted(jobId));
        }

        if (isJobAlreadyAllocated(jobId)) {
            throw new LinkServiceException("Job with id %d is already allocated!".formatted(jobId));
        } else {
            Link linkToBeUpdated = link.get();
            Job readJob = job.get();
            // TODO: check if job exists
            linkToBeUpdated.addJob(readJob);
            this.linkRepository.save(linkToBeUpdated);
        }
    }

    private boolean isJobAlreadyAllocated(long jobId) {
        return false;
    }

}
