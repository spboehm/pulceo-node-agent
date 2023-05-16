package dev.pulceo.pna.model.iperf3;


import lombok.Data;

import java.util.List;

@Data
public class Iperf3Result {

    private final String from;
    private final String to;
    private final String start;
    private final String end;
    private final List<String> iperf3Output;
    private final Iperf3BandwidthMeasurement client;
    private final Iperf3BandwidthMeasurement receiver;

}
