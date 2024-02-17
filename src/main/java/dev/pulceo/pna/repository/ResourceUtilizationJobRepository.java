package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.ResourceUtilizationJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceUtilizationJobRepository extends CrudRepository<ResourceUtilizationJob, Long> {
}
