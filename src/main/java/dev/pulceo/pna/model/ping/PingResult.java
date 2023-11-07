package dev.pulceo.pna.model.ping;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PingResult extends Resource {

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
}
