package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.registration.NodeRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRegistrationRepository extends JpaRepository<NodeRegistration, Long> {
}