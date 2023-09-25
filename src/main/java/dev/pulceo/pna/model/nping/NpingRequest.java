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

    public String getCmd() {
        if (isUDPClient()) {
            return String.format("/usr/bin/nping -4 --udp -c %s --dest-ip %s -p %s -e %s", rounds, destinationHost, port, iface);
        } else {
            return String.format("/usr/bin/nping -4 --tcp-connect -c %s --dest-ip %s -p %s -e %s", rounds, destinationHost, port, iface);
        }
    }

    public boolean isUDPClient() {
        if (this.npingClientProtocol == NpingClientProtocol.UDP) {
            return true;
        } else {
            return false;
        }
    }

}
