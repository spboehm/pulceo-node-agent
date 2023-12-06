package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.node.Node;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface NodeRepository extends CrudRepository<Node, Long> {

    @EntityGraph(value="graph.Node.jobs")
    Optional<Node> findByPnaUUID(String pnaUUID);

    Optional<Node> findByIsLocalNode(boolean isLocalNode);

    Optional<Node> findByUuid(UUID uuid);

}
