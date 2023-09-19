package dev.pulceo.pna.model.nping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NpingUDPResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
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
}
