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
public class Iperf3BandwidthMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Iperf3ClientProtocol iperf3Protocol;
    private int bitrate;
    private String bandwidthUnit = "Mbits/s";
    private Iperf3Role iperf3Role;

    public Iperf3BandwidthMeasurement(Iperf3ClientProtocol iperf3Protocol, int bitrate, Iperf3Role iperf3Role) {
        this.iperf3Protocol = iperf3Protocol;
        this.bitrate = bitrate;
        this.iperf3Role = iperf3Role;
    }
}
