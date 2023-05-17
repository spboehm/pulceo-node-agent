package dev.pulceo.pna.model.iperf3;

import lombok.Data;

import java.util.UUID;

@Data
public class Iperf3BandwidthMeasurement {
    private final Iperf3ClientProtocol iperf3Protocol;
    private final int bitrate;
    private final String bandwidthUnit = "Mbits/s";
    private final Iperf3Role iperf3Role;
}
