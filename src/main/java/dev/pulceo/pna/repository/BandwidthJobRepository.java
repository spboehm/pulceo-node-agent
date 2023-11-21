package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.registration.CloudRegistration;
import org.springframework.data.repository.CrudRepository;

public interface BandwidthJobRepository extends CrudRepository<CloudRegistration, Long> {

}
