package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.application.Application;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationRepository extends CrudRepository<Application, Long> {
}
