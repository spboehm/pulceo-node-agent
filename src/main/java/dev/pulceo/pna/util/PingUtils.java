package dev.pulceo.pna.util;

import dev.pulceo.pna.model.ping.PingDelayMeasurement;

import java.util.List;

public class PingUtils {

    public PingDelayMeasurement measureICMPDelay(List<String> processOutput) {
        return new PingDelayMeasurement();
    }

}
