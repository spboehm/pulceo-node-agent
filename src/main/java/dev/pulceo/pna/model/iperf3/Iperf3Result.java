package dev.pulceo.pna.model.iperf3;


import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Iperf3Result {
    private final String from;
    private final String to;
    private final String start;
    private final String end;
    private final List<Iperf3BandwidthMeasurement> iperf3BandwidthMeasurements;
}
