package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.PingException;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.model.ping.PingPacketStatistics;
import dev.pulceo.pna.model.ping.RttStatistics;

import java.util.List;

public class PingUtils {

    public static PingDelayMeasurement extractPingDelayMeasurement(List<String> pingOutput) throws PingException {
        for (int i = pingOutput.size() - 1; i >= 0; i--) {
            if (pingOutput.get(i).contains("---  ping statistics ---")) {
                PingPacketStatistics pingPacketStatistics = new PingPacketStatistics();
                if (pingOutput.size() > i+1) {
                    // process packetStatistics
                    pingPacketStatistics = extractPingPacketStatistics(pingOutput.get(i + 1));
                }
                RttStatistics rttStatistics = new RttStatistics();
                if (pingOutput.size() > i+2) {
                    // process packetStatistics
                    rttStatistics = extractRttStatistics(pingOutput.get(i + 2));
                }

                return new PingDelayMeasurement(pingPacketStatistics.getPacketsTransmitted(), pingPacketStatistics.getPacketsReceived(), pingPacketStatistics.getPacketLoss(), pingPacketStatistics.getTime(), rttStatistics.getRttMin(), rttStatistics.getRttAvg(), rttStatistics.getRttMax(), rttStatistics.getRttMdev());
            }
        }
        throw new PingException(pingOutput.get(0).toString());
    }

    private static PingPacketStatistics extractPingPacketStatistics(String resultLine) {
        if (resultLine.contains("packets")) {
            String[] splitResultLine = resultLine.split(",");
            int packetsTransmitted = parseIntValue(0, splitResultLine);
            int packetsReceived = parseIntValue(1, splitResultLine);
            float packetLoss = parseFloatValue(2, splitResultLine);
            int time = parseIntValue(3, splitResultLine);
            return new PingPacketStatistics(packetsTransmitted, packetsReceived, packetLoss,time);
        } else {
            return new PingPacketStatistics();
        }
    }

    private static RttStatistics extractRttStatistics(String resultLine) {
        if (resultLine.contains("rtt")) {
            String[] splitResultLine = resultLine.split("=");
            String[] splitRttValues = splitResultLine[1].split("/");
            float rttMin = parseFloatValue(0, splitRttValues);
            float rttAvg = parseFloatValue(1, splitRttValues);
            float rttMax = parseFloatValue(2, splitRttValues);
            float rttMdev = parseFloatValue(3, splitRttValues);
            return new RttStatistics(rttMin, rttAvg, rttMax, rttMdev);
        } else {
            return new RttStatistics();
        }
    }

    private static int parseIntValue(int index, String[] split) {
        return Integer.parseInt(split[index].replaceAll("[^0-9]", "").trim());
    }

    private static float parseFloatValue(int index, String[] split) {
        return Float.parseFloat(split[index].replaceAll("[^0-9|.]", "").trim());
    }

}
