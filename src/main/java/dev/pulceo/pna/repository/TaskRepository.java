package dev.pulceo.pna.repository;

import dev.pulceo.pna.model.task.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends CrudRepository<Task, Long> {
    Optional<Task> readTaskByUuid(UUID uuid);
}
