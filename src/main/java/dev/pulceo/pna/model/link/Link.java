package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.jobs.Job;
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
                @NamedAttributeNode("jobs")
        }
)
public class Link extends Resource  {
    private String name;
    private ResourceType resourceType;
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private long srcId;
    private long destId;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Job> jobs = new ArrayList<>();

    public Link(String name, ResourceType resourceType, long srcId, long destId) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcId = srcId;
        this.destId = destId;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
    }

}
