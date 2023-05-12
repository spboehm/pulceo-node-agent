package dev.pulceo.pna.util;

import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
import dev.pulceo.pna.model.iperf3.Iperf3Role;

import java.util.List;

public class Iperf3Utils {

    public static Iperf3BandwidthMeasurement extractBandwidth(Iperf3Protocol iperf3Protocol, List<String> iperf3Output, Iperf3Role iperf3Role) {

        int indexOfBitrate = 0;
        int indexOfRetr = 0;
        String resultLine = new String();
        // we iterate backwards through the iperf3 output, because results are expected to be at the end
        for (int i = iperf3Output.size() - 1; i >= 0; i--) {
            if (iperf3Output.get(i).contains("[ ID]")) {
                indexOfBitrate = iperf3Output.get(i).indexOf("Bitrate");
                indexOfRetr = iperf3Output.get(i).indexOf("Retr");
                break;
            }
            if (iperf3Output.get(i).contains(iperf3Role.toString().toLowerCase())) {
                resultLine = iperf3Output.get(i);
            }
        }
        // extract bitrate, it is located between
        int bitrate = Integer.valueOf(resultLine.substring(indexOfBitrate, indexOfRetr).replaceAll("[^0-9]", ""));
        return new Iperf3BandwidthMeasurement(iperf3Protocol, bitrate, iperf3Role);
    }

}
