package dev.pulceo.pna.model.ping;

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
@EqualsAndHashCode(callSuper = true, exclude = {"pingDelayMeasurement"})
public class PingResult extends Resource implements MetricResult {

    private String sourceHost;
    private String destinationHost;
    private String startTime;
    private String endTime;
    @OneToOne(cascade = {CascadeType.ALL})
    private PingDelayMeasurement pingDelayMeasurement;

    public PingResult(String sourceHost, String destinationHost, String startTime, String endTime, PingDelayMeasurement pingDelayMeasurement) {
        this.sourceHost = sourceHost;
        this.destinationHost = destinationHost;
        this.startTime = startTime;
        this.endTime = endTime;
        this.pingDelayMeasurement = pingDelayMeasurement;
    }

    @Override
    @JsonIgnore
    public UUID getUUID() {
        return super.getUuid();
    }

    @Override
    @JsonIgnore
    public MetricType getMetricType() {
        return MetricType.PING_ICMP;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getResultData() {
        return Map.of(
                "sourceHost", sourceHost,
                "destinationHost", destinationHost,
                "startTime", startTime,
                "endTime", endTime,
                "pingDelayMeasurement", pingDelayMeasurement
        );
    }
}
