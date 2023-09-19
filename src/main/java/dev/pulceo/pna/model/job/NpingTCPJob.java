package dev.pulceo.pna.model.job;

import dev.pulceo.pna.model.nping.NpingClientProtocol;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NpingTCPJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private NpingClientProtocol npingClientProtocol;
    private int recurrence;
    private boolean enabled = false;

    public NpingTCPJob(String sourceHost, String destinationHost, int port, NpingClientProtocol npingClientProtocol, int recurrence) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.npingClientProtocol = npingClientProtocol;
        this.recurrence = recurrence;
    }
}
