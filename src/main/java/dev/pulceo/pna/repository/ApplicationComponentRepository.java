package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.application.ApplicationComponent;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ApplicationComponentRepository extends CrudRepository<ApplicationComponent, Long>{
    Optional<ApplicationComponent> findByName(String name);

    Optional<ApplicationComponent> findByPort(int port);
}
