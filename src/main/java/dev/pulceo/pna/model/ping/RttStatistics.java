package dev.pulceo.pna.model.ping;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RttStatistics {

    private float rttMin;
    private float rttAvg;
    private float rttMax;
    private float rttMdev;

    public RttStatistics(float rttMin, float rttAvg, float rttMax, float rttMdev) {
        this.rttMin = rttMin;
        this.rttAvg = rttAvg;
        this.rttMax = rttMax;
        this.rttMdev = rttMdev;
    }
}