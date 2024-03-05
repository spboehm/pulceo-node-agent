package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface SuperJobRespository extends CrudRepository<Job, Long> {

    Optional<Job> findByUuid(UUID jobUUID);
}
