package dev.pulceo.pna.util;

import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.model.ping.PingPacketStatistics;
import dev.pulceo.pna.model.ping.RttStatistics;

import java.util.List;

public class PingUtils {

    public static PingDelayMeasurement extractPingDelayMeasurement(List<String> pingOutput) {
        /*
        Iterate backwards
         --- ping statistics ---
         all subsequent lines
         */

        // --- ping statistics --- [0]
        for (int i = pingOutput.size() - 1; i >= 0; i--) {
            if (pingOutput.get(i).contains("---  ping statistics ---")) {
                // process packetStatistics
                PingPacketStatistics pingPacketStatistics = extractPingPacketStatistics(pingOutput.get(i + 1));

                // process rtts
                RttStatistics rttStatistics = extractRttStatistics(pingOutput.get(i + 2));
                return new PingDelayMeasurement(pingPacketStatistics.getPacketsTransmitted(), pingPacketStatistics.getPacketsReceived(), pingPacketStatistics.getPacketLoss(), pingPacketStatistics.getTime(), rttStatistics.getRttMin(), rttStatistics.getRttAvg(), rttStatistics.getRttMax(), rttStatistics.getRttMdev());
            }
        }
        throw new RuntimeException();
    }



    private static PingPacketStatistics extractPingPacketStatistics(String resultLine) {
        // line contains "packets" [1]
        // Split by ,
        String[] splitResultLine = resultLine.split(",");
        int packetsTransmitted = parseIntValue(0, splitResultLine);
        int packetsReceived = parseIntValue(1, splitResultLine);
        float packetLoss = parseFloatValue(2, splitResultLine);
        int time = parseIntValue(3, splitResultLine);
        return new PingPacketStatistics(packetsTransmitted, packetsReceived, packetLoss,time);
    }

    private static RttStatistics extractRttStatistics(String resultLine) {
        // line contains rtt [2]
        // Split at =
        // Split by /
        String[] splitResultLine = resultLine.split("=");
        String[] splitRttValues = splitResultLine[1].split("/");
        float rttMin = parseFloatValue(0, splitRttValues);
        float rttAvg = parseFloatValue(1, splitRttValues);
        float rttMax = parseFloatValue(2, splitRttValues);
        float rttMdev = parseFloatValue(3, splitRttValues);
        return new RttStatistics(rttMin, rttAvg, rttMax, rttMdev);
    }

    private static int parseIntValue(int index, String[] split) {
        return Integer.parseInt(split[index].replaceAll("[^0-9]", "").trim());
    }

    private static float parseFloatValue(int index, String[] split) {
        return Float.parseFloat(split[index].replaceAll("[^0-9|.]", "").trim());
    }



}
