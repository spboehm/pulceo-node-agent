package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.Job;
import dev.pulceo.pna.model.node.Node;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"jobs"})
@ToString(callSuper = true)
@NamedEntityGraph(
        name = "graph.Link.jobs",
        attributeNodes = {
                @NamedAttributeNode("srcNode"),
                @NamedAttributeNode("destNode"),
                @NamedAttributeNode("jobs")
        }
)
public class Link extends Resource  {
    private String name;
    private ResourceType resourceType;
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    // set cascade type
    @OneToOne
    private Node srcNode;
    // set cascade type
    @OneToOne
    private Node destNode;
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();

    public Link(String name, ResourceType resourceType, Node srcNode, Node destNode) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcNode = srcNode;
        this.destNode = destNode;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
        job.setLink(this);
    }

}
