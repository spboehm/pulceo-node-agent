package dev.pulceo.pna.model.iperf3;

import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
public class IperfUDPBandwidthMeasurement extends IperfBandwidthMeasurement {

    private float jitter;
    private int lostDatagrams;
    private int totalDatagrams;

    public IperfUDPBandwidthMeasurement(IperfClientProtocol iperf3Protocol, int bitrate, IperfRole iperfRole, float jitter, int lostDatagrams, int totalDatagrams) {
        super(iperf3Protocol, bitrate, iperfRole);
        this.jitter = jitter;
        this.lostDatagrams = lostDatagrams;
        this.totalDatagrams = totalDatagrams;
    }

    @Override
    public String toString() {
        return "IperfUDPBandwidthMeasurement{" +
                "jitter=" + jitter +
                ", lostDatagrams=" + lostDatagrams +
                ", totalDatagrams=" + totalDatagrams +
                "} " + super.toString();
    }
}
