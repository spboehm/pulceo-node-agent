package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public abstract class Job extends Resource {
    // default false
    private boolean enabled = false;
}
