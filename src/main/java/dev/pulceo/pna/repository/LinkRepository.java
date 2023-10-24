package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.node.Node;
import org.springframework.data.repository.CrudRepository;

public interface LinkRepository extends CrudRepository<Node, Long> {

}
