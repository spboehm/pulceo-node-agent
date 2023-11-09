package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.PingJob;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PingJobRepository extends CrudRepository<PingJob, Long> {

    @Override
    Optional<PingJob> findById(Long aLong);
}
