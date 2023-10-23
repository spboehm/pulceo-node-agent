package dev.pulceo.pna.service;

import dev.pulceo.pna.model.link.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {

    @Autowired
    LinkService linkService;

    public long createLink(Link link) {

        return 0;
    }

}
