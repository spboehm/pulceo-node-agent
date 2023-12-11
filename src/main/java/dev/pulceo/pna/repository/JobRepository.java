package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.Job;
import org.springframework.data.repository.CrudRepository;

public interface JobRepository extends CrudRepository<Job, Long> {
}
