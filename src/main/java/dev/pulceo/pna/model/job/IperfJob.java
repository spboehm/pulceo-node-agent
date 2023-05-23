package dev.pulceo.pna.model.job;

import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IperfJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private IperfClientProtocol iperfClientProtocol;
    private int recurrence;
    private boolean active = false;

    public IperfJob(String sourceHost, String destinationHost, int port, IperfClientProtocol iperfClientProtocol, int recurrence) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.iperfClientProtocol = iperfClientProtocol;
        this.recurrence = recurrence;
    }

}
