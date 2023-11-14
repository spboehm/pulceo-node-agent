package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.link.Link;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true, exclude = {"link"})
public abstract class LinkJob extends Job {

    // This is the LinkJob relationship

    @ManyToOne(fetch = FetchType.LAZY)
    private Link link;

}
