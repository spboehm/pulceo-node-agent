package dev.pulceo.pna.model.ping;

import lombok.Data;

@Data
public class RttStatistics {

    private final float rttMin;
    private final float rttAvg;
    private final float rttMax;
    private final float rttMdev;

    public RttStatistics(float rttMin, float rttAvg, float rttMax, float rttMdev) {
        this.rttMin = rttMin;
        this.rttAvg = rttAvg;
        this.rttMax = rttMax;
        this.rttMdev = rttMdev;
    }
}