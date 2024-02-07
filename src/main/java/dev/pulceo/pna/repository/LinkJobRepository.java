package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.jobs.LinkJob;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LinkJobRepository extends CrudRepository<LinkJob, Long>  {
    List<LinkJob> findByEnabled(boolean enabled);
}
