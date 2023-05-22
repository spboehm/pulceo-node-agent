package dev.pulceo.pna.model.iperf3;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IperfResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private String startTime;
    private String endTime;
    @OneToOne(cascade = {CascadeType.ALL})
    private Iperf3BandwidthMeasurement iperf3BandwidthMeasurementReceiver;
    @OneToOne(cascade = {CascadeType.ALL})
    private Iperf3BandwidthMeasurement iperf3BandwidthMeasurementSender;

    public IperfResult(String sourceHost, String destinationHost, String startTime, String endTime, Iperf3BandwidthMeasurement iperf3BandwidthMeasurementReceiver, Iperf3BandwidthMeasurement iperf3BandwidthMeasurementSender) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.startTime = startTime;
        this.endTime = endTime;
        this.iperf3BandwidthMeasurementReceiver = iperf3BandwidthMeasurementReceiver;
        this.iperf3BandwidthMeasurementSender = iperf3BandwidthMeasurementSender;
    }
}
