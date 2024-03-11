package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.application.Application;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends CrudRepository<Application, Long> {

    @EntityGraph(value="graph.Application.applicationComponents")
    Optional<Application> findByName(String name);

    @EntityGraph(value="graph.Application.applicationComponents")
    Optional<Application> findByUuid(UUID applicationUUID);

    @EntityGraph(value="graph.Application.applicationComponents")
    Iterable<Application> findAll();
}
