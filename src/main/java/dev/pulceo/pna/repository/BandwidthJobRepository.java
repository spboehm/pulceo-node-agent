package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.tasks.IperfTask;
import org.springframework.data.repository.CrudRepository;

public interface BandwidthJobRepository extends CrudRepository<IperfTask, Long> {

}
