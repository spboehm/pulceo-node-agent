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
public class IperfBandwidthMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private IperfClientProtocol iperf3Protocol;
    private int bitrate;
    private String bandwidthUnit = "Mbits/s";
    private IperfRole iperfRole;

    public IperfBandwidthMeasurement(IperfClientProtocol iperf3Protocol, int bitrate, IperfRole iperfRole) {
        this.iperf3Protocol = iperf3Protocol;
        this.bitrate = bitrate;
        this.iperfRole = iperfRole;
    }
}
