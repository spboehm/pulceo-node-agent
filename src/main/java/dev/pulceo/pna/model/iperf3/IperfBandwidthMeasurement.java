package dev.pulceo.pna.model.iperf3;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@NoArgsConstructor
public class IperfBandwidthMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private IperfClientProtocol iperf3Protocol;
    private float bitrate;
    private String bandwidthUnit = "Mbits/s";
    private IperfRole iperfRole;

    public IperfBandwidthMeasurement(IperfClientProtocol iperf3Protocol, float bitrate, IperfRole iperfRole) {
        this.iperf3Protocol = iperf3Protocol;
        this.bitrate = bitrate;
        this.iperfRole = iperfRole;
    }
}
