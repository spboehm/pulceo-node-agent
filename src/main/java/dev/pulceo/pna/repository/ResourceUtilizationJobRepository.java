package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ResourceUtilizationJobRepository extends CrudRepository<ResourceUtilizationJob, Long> {
    Optional<ResourceUtilizationJob> findByUuid(UUID uuid);
}
