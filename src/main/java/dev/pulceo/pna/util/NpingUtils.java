package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;

import java.util.List;

public class NpingUtils {

    public static NpingTCPDelayMeasurement extractNpingTCPDelayMeasurement(NpingClientProtocol npingClientProtocol, List<String> npingOutput) throws NpingException {
        // only for TCP
        int tcpConnectionAttempts = 0;
        int tcpSuccessfulConnections = 0;
        int tcpFailedAbsolute = 0;
        double tcpFailedRelative = 0.00;

        // for both UDP and TCP
        double maxRTT = 0.000;
        double minRTT = 0.000;
        double avgRTT = 0.000;
        try {
            for (int i = npingOutput.size() - 1; i >= 0; i--) {
                // first protocol-specific metrics
                if (npingOutput.get(i).contains("TCP connection attempts")) {
                    // split string
                    String[] split = npingOutput.get(i).split("\\|");
                    tcpConnectionAttempts = parseFirstParameter(split);
                    tcpSuccessfulConnections = parseSecondParameter(split);
                    tcpFailedAbsolute = parseThirdParameter(split);
                    tcpFailedRelative = parseFourthParameter(split);
                }
                if (npingOutput.get(i).contains("Max rtt")) {
                    // second measurements for both UDP and TCP
                    // split spring
                    String[] split = npingOutput.get(i).split("\\|");
                    maxRTT = getMaxRTT(split);
                    minRTT = getMinRTT(split);
                    avgRTT = getAvgRTT(split);
                    break;
                }
            }
        } catch (NumberFormatException e) {
            throw new NpingException("Could not obtain nping TCP delay mesurement!");
        }
        return new NpingTCPDelayMeasurement(maxRTT,minRTT,avgRTT,tcpConnectionAttempts,tcpSuccessfulConnections,tcpFailedAbsolute,tcpFailedRelative);
    }

    private static double parseFourthParameter(String[] split) {
        return Double.parseDouble(split[2].substring(split[2].indexOf("(") + 1, split[2].indexOf(")") - 1));
    }

    private static int parseThirdParameter(String[] split) {
        return Integer.parseInt(split[2].substring(0, split[2].indexOf("(")).replaceAll("[^0-9]", "").trim());
    }

    private static int parseSecondParameter(String[] split) {
        return Integer.parseInt(split[1].replaceAll("[^0-9]", "").trim());
    }

    private static int parseFirstParameter(String[] split) {
        return Integer.parseInt(split[0].replaceAll("[^0-9]", "").trim());
    }

    private static double getAvgRTT(String[] split) {
        return Double.parseDouble(split[2].replaceAll("[^0-9|.]", "").trim());
    }

    private static double getMinRTT(String[] split) {
        return Double.parseDouble(split[1].replaceAll("[^0-9|.]", "").trim());
    }

    private static double getMaxRTT(String[] split) {
        return Double.parseDouble(split[0].replaceAll("[^0-9|.]", "").trim());
    }

    public static NpingUDPDelayMeasurement extractNpingUDPDelayMeasurement(NpingClientProtocol npingClientProtocol, List<String> npingOutput) throws NpingException {
        // only for UDP
        int udpPacketsSent = 0;
        int udpReceivedPackets = 0;
        int udpLostPacketsAbsolute = 0;
        double udpLostPacketsRelative = 0.00;

        // for both UDP and TCP
        double maxRTT = 0.000;
        double minRTT = 0.000;
        double avgRTT = 0.000;
        try {
            for (int i = npingOutput.size() - 1; i >= 0; i--) {
                // first protocol-specific metrics
                if (npingOutput.get(i).contains("UDP packets sent")) {
                    // split string
                    String[] split = npingOutput.get(i).split("\\|");
                    udpPacketsSent = parseFirstParameter(split);
                    udpReceivedPackets = parseSecondParameter(split);
                    udpLostPacketsAbsolute = parseThirdParameter(split);
                    udpLostPacketsRelative = parseFourthParameter(split);
                }
                if (npingOutput.get(i).contains("Max rtt")) {
                    // second measurements for both UDP and TCP
                    // split spring
                    String[] split = npingOutput.get(i).split("\\|");
                    maxRTT = getMaxRTT(split);
                    minRTT = getMinRTT(split);
                    avgRTT = getAvgRTT(split);
                    break;
                }

            }
        } catch (NumberFormatException e) {
            throw new NpingException("Could not obtain nping UDP delay mesurement!");
        }
        return new NpingUDPDelayMeasurement(maxRTT,minRTT,avgRTT,udpPacketsSent,udpReceivedPackets,udpLostPacketsAbsolute,udpLostPacketsRelative);
    }

    public static boolean isUDP(String cmd) {
        if (cmd.contains("--udp")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTCP(String cmd) {
        if (cmd.contains("--tcp-connect")) {
            return true;
        } else {
            return false;
        }
    }

    public static String extractHostFromNpingCmd(String cmd) {
        // find position of --dest-ip
        int indexOfHost = cmd.indexOf("--dest-ip");
        int indexOfNextCommand = cmd.indexOf("-p");
        return cmd.substring(indexOfHost + 10, indexOfNextCommand - 1);
    }

}
