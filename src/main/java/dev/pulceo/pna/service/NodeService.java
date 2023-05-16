package dev.pulceo.pna.service;

import dev.pulceo.pna.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NodeService {

    @Autowired
    NodeRepository nodeRepository;

}
