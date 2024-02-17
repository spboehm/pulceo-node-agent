package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.NodeJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeJobRepository extends CrudRepository<NodeJob, Long> {

}
