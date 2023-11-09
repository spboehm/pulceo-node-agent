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
                @NamedAttributeNode("name"),
                @NamedAttributeNode("resourceType"),
                @NamedAttributeNode("linkDirectionType"),
                @NamedAttributeNode("srcId"),
                @NamedAttributeNode("destId"),
                @NamedAttributeNode("jobs")
        }
)
public class Link extends Resource  {
    private String name;
    private ResourceType resourceType;
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private long srcId;
    private long destId;
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Job> jobs = new ArrayList<>();

    public Link(String name, ResourceType resourceType, long srcId, long destId) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcId = srcId;
        this.destId = destId;
    }

    public void addJob(Job job) {
        this.jobs.add(job);
        job.setLink(this);
    }
//
//    public boolean jobExists(Job job) {
//        return this.jobs.contains(job);
//    }

//    public void setNpingJob(NpingJob npingJob) {
//        if (npingJob.getNpingRequest().getNpingClientProtocol() == NpingClientProtocol.TCP) {
//            this.setNpingTCPJob(npingJob);
//        } else {
//            this.setNpingUDPJob(npingJob);
//        }
//    }
//
//    public void setIperfJob(IperfJob iperfJob) {
//        if (iperfJob.getIperfRequest().getIperfClientProtocol() == IperfClientProtocol.TCP) {
//            this.setIperfTCPJob(iperfJob);
//        } else {
//            this.setIperfUDPJob(iperfJob);
//        }
//
//    }

}
