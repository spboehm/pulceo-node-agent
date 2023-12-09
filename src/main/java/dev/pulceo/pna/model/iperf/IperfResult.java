package dev.pulceo.pna.model.iperf;


import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.message.MetricResult;
import dev.pulceo.pna.model.message.MetricType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"iperfBandwidthMeasurementReceiver", "iperfBandwidthMeasurementSender"})
public class IperfResult extends Resource implements MetricResult {
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

    @Override
    @JsonIgnore
    public UUID getUUID() {
        return super.getUuid();
    }

    @Override
    @JsonIgnore
    public MetricType getMetricType() {
        return MetricType.UDP_BW;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getResultData() {
        return Map.of(
                "sourceHost", sourceHost,
                "destinationHost", destinationHost,
                "startTime", startTime,
                "endTime", endTime,
                "iperfBandwidthMeasurementReceiver", iperfBandwidthMeasurementReceiver,
                "iperfBandwidthMeasurementSender", iperfBandwidthMeasurementSender
        );
    }
}
