package dev.pulceo.pna.model.nping;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NpingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private NpingClientProtocol npingClientProtocol;
    private int rounds;
    private String iface;

    public NpingRequest(String sourceHost, String destinationHost, int port, NpingClientProtocol npingClientProtocol, int rounds, String iface) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.npingClientProtocol = npingClientProtocol;
        this.rounds = rounds;
        this.iface = iface;
    }

    public NpingClientCmd toNpingClientCmd() {
        return new NpingClientCmd(this.npingClientProtocol, this.port, this.rounds, this.destinationHost, this.iface);
    }


}
