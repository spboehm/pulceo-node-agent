package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public class SuperJob extends Resource {



}
