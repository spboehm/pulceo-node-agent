package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.link.Link;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public abstract class Job extends SuperJob {

    // This is the LinkJob relationship

    @ManyToOne(fetch = FetchType.LAZY)
    private Link link;

}
