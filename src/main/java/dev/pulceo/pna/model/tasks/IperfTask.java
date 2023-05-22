package dev.pulceo.pna.model.tasks;

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
public class IperfTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private IperfClientProtocol iperfClientProtocol;
    private int recurrence;

    public IperfTask(String from, String to, int port, IperfClientProtocol iperfClientProtocol, int recurrence) {
        this.sourceHost = from;
        this.destinationHost = to;
        this.port = port;
        this.iperfClientProtocol = iperfClientProtocol;
        this.recurrence = recurrence;
    }

}
