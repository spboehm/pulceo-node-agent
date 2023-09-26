package dev.pulceo.pna.model.ping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PingDelayMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    private int packetsTransmitted;
    private int packetsReceived;
    private float packetLoss;
    private int time;
    private float rttMin;
    private float rttAvg;
    private float rttMax;
    private float rttMdev;

    public PingDelayMeasurement(int packetsTransmitted, int packetsReceived, float packetLoss, int time, float rttMin, float rttAvg, float rttMax, float rttMdev) {
        this.packetsTransmitted = packetsTransmitted;
        this.packetsReceived = packetsReceived;
        this.packetLoss = packetLoss;
        this.time = time;
        this.rttMin = rttMin;
        this.rttAvg = rttAvg;
        this.rttMax = rttMax;
        this.rttMdev = rttMdev;
    }


}
