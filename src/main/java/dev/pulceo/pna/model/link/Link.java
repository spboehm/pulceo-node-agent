package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.LinkJob;
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
@EqualsAndHashCode(callSuper = true, exclude = {"linkJobs"})
@ToString(callSuper = true, exclude = {"linkJobs"})
@NamedEntityGraph(
        name = "graph.Link.jobs",
        attributeNodes = {
                @NamedAttributeNode("srcNode"),
                @NamedAttributeNode("destNode"),
                @NamedAttributeNode("linkJobs")
        }
)
public class Link extends Resource  {
    private String name;
    private ResourceType resourceType;

    @Builder.Default
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    // set cascade type
    @OneToOne
    private Node srcNode;
    // set cascade type
    @OneToOne
    private Node destNode;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinkJob> linkJobs = new ArrayList<>();

    public Link(String name, ResourceType resourceType, Node srcNode, Node destNode) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcNode = srcNode;
        this.destNode = destNode;
    }

    public void addJob(LinkJob linkJob) {
        this.linkJobs.add(linkJob);
        linkJob.setLink(this);
    }

}
