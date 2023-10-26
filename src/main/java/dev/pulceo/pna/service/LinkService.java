package dev.pulceo.pna.service;

import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.repository.LinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    LinkRepository linkRepository;

    @Autowired
    NodeService nodeService;

    public long createLink(Link link) {
        // TODO ensure that resources exists
        return this.linkRepository.save(link).getId();
    }



}
