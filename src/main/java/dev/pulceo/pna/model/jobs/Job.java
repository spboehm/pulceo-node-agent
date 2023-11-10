package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.link.Link;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public abstract class Job extends Resource {

    @ManyToOne(fetch = FetchType.LAZY)
    private Link link;

}
