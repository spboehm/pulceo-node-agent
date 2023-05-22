package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.job.IperfJob;
import org.springframework.data.repository.CrudRepository;

public interface BandwidthJobRepository extends CrudRepository<IperfJob, Long> {

}
