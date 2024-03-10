package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.node.Node;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true, exclude = {"node"})
public abstract class NodeJob extends Job {

    @ManyToOne(fetch = FetchType.LAZY)
    private Node node;

    @ManyToOne(fetch = FetchType.LAZY)
    private Application application;

}
