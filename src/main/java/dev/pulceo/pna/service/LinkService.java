package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.LinkServiceException;
import dev.pulceo.pna.model.jobs.Job;
import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LinkService {

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    JobService jobService;

    @Autowired
    NodeService nodeService;

    public long createLink(Link link) throws LinkServiceException {
        if (nodeService.readNode(link.getSrcId()).isEmpty()) {
            throw new LinkServiceException("Source node with id %d does not exist!".formatted(link.getSrcId()));
        } else if (nodeService.readNode(link.getDestId()).isEmpty()) {
            throw new LinkServiceException("Destination node with id %d does not exist!".formatted(link.getDestId()));
        }
        return this.linkRepository.save(link).getId();
    }

    public Optional<Link> readLink(long id) {
        return this.linkRepository.findById(id);
    }

    public List<Link> readAllLinks() {
        return this.linkRepository.findAll();
    }

    public Optional<Link> readLinkByDestNode(long id) { return this.linkRepository.findLinkByDestId(id); }

    public void addJob(long linkId, Job job) {
        // TODO: check if link exists AND check if job exists AND if job is not already allocated?
        if (checkIfLinkExists(linkId) && checkIfJobExists(job) && checkIfJobIsNotAlreadyAllocated(job)) {

        }



    }

    private boolean checkIfLinkExists(long linkId) {
        return this.readLink(linkId).isPresent();
    }


    private boolean checkIfJobExists(Job job) {
        switch (job.getJobType()) {
            case PING: return this.jobService.readPingJobOptional(job.getId()).isPresent();
            default: return false;
        }
    }

    private boolean checkIfJobIsNotAlreadyAllocated(Job job) {
        // TODO: implement here
        return true;
    }

}
