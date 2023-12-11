package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.link.Link;
import dev.pulceo.pna.model.node.Node;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LinkRepository extends CrudRepository<Link, Long> {

    // consider https://stackoverflow.com/questions/64209863/spring-jpa-data-multiple-methods-with-same-functionality

    @Override
    @EntityGraph(value="graph.Link.jobs")
    Optional<Link> findById(Long id);

    @Override
    List<Link> findAll();

    Optional<Link> findLinkByDestNode(Node destNode);

    @EntityGraph(value="graph.Link.jobs")
    Optional<Link> findByUuid(UUID uuid);

}
