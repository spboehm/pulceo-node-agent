package dev.pulceo.pna.model.iperf;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class IperfResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private String sourceHost;
    private String destinationHost;
    private String startTime;
    private String endTime;
    @OneToOne(cascade = {CascadeType.ALL})
    private IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver;
    @OneToOne(cascade = {CascadeType.ALL})
    private IperfBandwidthMeasurement iperfBandwidthMeasurementSender;

    public IperfResult(String sourceHost, String destinationHost, String startTime, String endTime, IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver, IperfBandwidthMeasurement iperfBandwidthMeasurementSender) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.startTime = startTime;
        this.endTime = endTime;
        this.iperfBandwidthMeasurementReceiver = iperfBandwidthMeasurementReceiver;
        this.iperfBandwidthMeasurementSender = iperfBandwidthMeasurementSender;
    }
}
