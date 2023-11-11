package dev.pulceo.pna.model.ping;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PingDelayMeasurement extends Resource {

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
