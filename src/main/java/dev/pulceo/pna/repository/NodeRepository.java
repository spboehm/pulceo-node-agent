package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.node.Node;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface NodeRepository extends CrudRepository<Node, Long> {

    Optional<Node> findByPnaId(String pnaId);

    Optional<Node> findByIsLocalNode(boolean isLocalNode);



}
