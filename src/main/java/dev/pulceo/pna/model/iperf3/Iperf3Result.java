package dev.pulceo.pna.model.iperf3;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class Iperf3Result {

    private final String from;
    private final String to;
    private final String start = Instant.now().toString();
    private final String end;
    private final List<String> iperf3Output;
    private final Iperf3BandwidthMeasurement client;
    private final Iperf3BandwidthMeasurement receiver;

}
