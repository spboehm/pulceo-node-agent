package dev.pulceo.pna.util;

import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;

import java.util.List;

public class NpingUtils {

    public static NpingTCPDelayMeasurement extractNpingTCPDelayMeasurement(NpingClientProtocol npingClientProtocol, List<String> npingOutput) {
        // only for TCP
        int tcpConnectionAttempts = 0;
        int tcpSuccessfulConnections = 0;
        int tcpFailedAbsolute = 0;
        double tcpFailedRelative = 0.00;

        // for both UDP and TCP
        double maxRTT = 0.000;
        double minRTT = 0.000;
        double avgRTT = 0.000;

        for (int i = npingOutput.size() - 1; i >= 0; i--) {
            // first protocol-specific metrics
            if (npingOutput.get(i).contains("TCP connection attempts")) {
                // split string
                String[] split = npingOutput.get(i).split("\\|");
                tcpConnectionAttempts = Integer.parseInt(split[0].replaceAll("[^0-9]", "").trim());
                tcpSuccessfulConnections = Integer.parseInt(split[1].replaceAll("[^0-9]", "").trim());
                tcpFailedAbsolute = Integer.parseInt(split[2].substring(0, split[2].indexOf("(")).replaceAll("[^0-9]", "").trim());
                tcpFailedRelative = Double.parseDouble(split[2].substring(split[2].indexOf("(") + 1, split[2].indexOf(")")-1));
            }
            if (npingOutput.get(i).contains("Max rtt")) {
                // second measurements for both UDP and TCP
                // split spring
                String[] split = npingOutput.get(i).split("\\|");
                maxRTT = Double.parseDouble(split[0].replaceAll("[^0-9|.]", "").trim());
                minRTT = Double.parseDouble(split[1].replaceAll("[^0-9|.]", "").trim());
                avgRTT = Double.parseDouble(split[2].replaceAll("[^0-9|.]", "").trim());
                break;
            }
        }
        return new NpingTCPDelayMeasurement(maxRTT,minRTT,avgRTT,tcpConnectionAttempts,tcpSuccessfulConnections,tcpFailedAbsolute,tcpFailedRelative);
    }

    public static NpingUDPDelayMeasurement extractNpingUDPDelayMeasurement(NpingClientProtocol npingClientProtocol, List<String> npingOutput) {
        // only for UDP
        int udpPacketsSent = 0;
        int udpReceivedPackets = 0;
        int udpLostPacketsAbsolute = 0;
        double udpLostPacketsRelative = 0.00;

        // for both UDP and TCP
        double maxRTT = 0.000;
        double minRTT = 0.000;
        double avgRTT = 0.000;

        for (int i = npingOutput.size() - 1; i >= 0; i--) {
            // first protocol-specific metrics
            if (npingOutput.get(i).contains("UDP packets sent")) {
                // split string
                String[] split = npingOutput.get(i).split("\\|");
                udpPacketsSent = Integer.parseInt(split[0].replaceAll("[^0-9]", "").trim());
                udpReceivedPackets = Integer.parseInt(split[1].replaceAll("[^0-9]", "").trim());
                udpLostPacketsAbsolute = Integer.parseInt(split[2].substring(0, split[2].indexOf("(")).replaceAll("[^0-9]", "").trim());
                udpLostPacketsRelative = Double.parseDouble(split[2].substring(split[2].indexOf("(") + 1, split[2].indexOf(")")-1));
            }
            if (npingOutput.get(i).contains("Max rtt")) {
                // second measurements for both UDP and TCP
                // split spring
                String[] split = npingOutput.get(i).split("\\|");
                maxRTT = Double.parseDouble(split[0].replaceAll("[^0-9|.]", "").trim());
                minRTT = Double.parseDouble(split[1].replaceAll("[^0-9|.]", "").trim());
                avgRTT = Double.parseDouble(split[2].replaceAll("[^0-9|.]", "").trim());
                break;
            }

        }
        return new NpingUDPDelayMeasurement(maxRTT,minRTT,avgRTT,udpPacketsSent,udpReceivedPackets,udpLostPacketsAbsolute,udpLostPacketsRelative);
    }
}
