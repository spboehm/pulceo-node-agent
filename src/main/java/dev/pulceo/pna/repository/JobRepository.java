package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.LinkJob;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<LinkJob, Long>  {
}
