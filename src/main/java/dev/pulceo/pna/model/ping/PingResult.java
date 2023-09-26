package dev.pulceo.pna.model.ping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PingResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
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
