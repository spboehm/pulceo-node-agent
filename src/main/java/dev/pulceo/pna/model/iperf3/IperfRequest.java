package dev.pulceo.pna.model.iperf3;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IperfRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private IperfClientProtocol iperfClientProtocol;

    public IperfRequest(String sourceHost, String destinationHost, int port, IperfClientProtocol iperfClientProtocol) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.port = port;
        this.iperfClientProtocol = iperfClientProtocol;
    }
}
