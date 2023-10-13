package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.PingJob;
import org.springframework.data.repository.CrudRepository;

public interface PingJobRepository extends CrudRepository<PingJob, Long> {
}
