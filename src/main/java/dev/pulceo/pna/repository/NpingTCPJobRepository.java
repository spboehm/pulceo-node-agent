package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.NpingJob;
import org.springframework.data.repository.CrudRepository;

public interface NpingTCPJobRepository extends CrudRepository<NpingJob, Long> {

}
