package dev.pulceo.pna.model;

import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BandwidthJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private int port;
    private Iperf3ClientProtocol iperf3ClientProtocol;
    private int recurrence;

    public BandwidthJob(String from, String to, int port, Iperf3ClientProtocol iperf3ClientProtocol, int recurrence) {
        this.sourceHost = from;
        this.destinationHost = to;
        this.port = port;
        this.iperf3ClientProtocol = iperf3ClientProtocol;
        this.recurrence = recurrence;
    }

}
