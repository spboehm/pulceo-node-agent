package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Job extends Resource {

}
