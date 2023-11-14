package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.node.Node;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Inheritance(strategy = InheritanceType.JOINED)
@EqualsAndHashCode(callSuper = true)
public abstract class NodeJob extends SuperJob {

    @ManyToOne(fetch = FetchType.LAZY)
    private Node node;

}
