package dev.pulceo.pna.model.link;


import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.ResourceType;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.jobs.IperfJob;
import dev.pulceo.pna.model.jobs.NpingJob;
import dev.pulceo.pna.model.jobs.PingJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Link extends Resource  {

    private String name;
    private ResourceType resourceType;
    @Builder.Default
    private LinkDirectionType linkDirectionType = LinkDirectionType.UNDIRECTED;
    private long srcId;
    private long destId;
    @OneToOne
    private PingJob pingJob;
    @OneToOne
    private NpingJob npingTCPJob;
    @OneToOne
    private NpingJob npingUDPJob;
    @OneToOne
    private IperfJob iperfTCPJob;
    @OneToOne
    private IperfJob iperfUDPJob;

    public Link(String name, ResourceType resourceType, long srcId, long destId) {
        this.name = name;
        this.resourceType = resourceType;
        this.srcId = srcId;
        this.destId = destId;
    }

    public void setNpingJob(NpingJob npingJob) {
        if (npingJob.getNpingRequest().getNpingClientProtocol() == NpingClientProtocol.TCP) {
            this.setNpingTCPJob(npingJob);
        } else {
            this.setNpingUDPJob(npingJob);
        }
    }

    public void setIperfJob(IperfJob iperfJob) {
        if (iperfJob.getIperfRequest().getIperfClientProtocol() == IperfClientProtocol.TCP) {
            this.setIperfTCPJob(iperfJob);
        } else {
            this.setIperfUDPJob(iperfJob);
        }

    }

}
