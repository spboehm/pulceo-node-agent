package dev.pulceo.pna.model.nping;

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
@EqualsAndHashCode(callSuper = true, exclude = {"npingUDPDelayMeasurement"})
public class NpingUDPResult extends Resource implements MetricResult {

    private String sourceHost;
    private String destinationHost;
    private String startTime;
    private String endTime;
    private int dataLength;
    @OneToOne(cascade = {CascadeType.ALL})
    private NpingUDPDelayMeasurement npingUDPDelayMeasurement;

    public NpingUDPResult(String sourceHost, String destinationHost, String startTime, String endTime, int dataLength, NpingUDPDelayMeasurement npingUDPDelayMeasurement) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dataLength = dataLength;
        this.npingUDPDelayMeasurement = npingUDPDelayMeasurement;
    }

    @Override
    @JsonIgnore
    public UUID getUUID() {
        return super.getUuid();
    }

    @Override
    @JsonIgnore
    public MetricType getMetricType() {
        return MetricType.UDP_RTT;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getResultData() {
        return Map.of(
                "sourceHost", sourceHost,
                "destinationHost", destinationHost,
                "startTime", startTime,
                "endTime", endTime,
                "npingUDPDelayMeasurement", npingUDPDelayMeasurement
        );
    }
}
