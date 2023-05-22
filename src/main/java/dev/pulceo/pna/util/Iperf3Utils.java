package dev.pulceo.pna.util;

import dev.pulceo.pna.model.iperf3.IperfBandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfRole;

import java.util.List;

public class Iperf3Utils {

    public static IperfBandwidthMeasurement extractIperf3BandwidthMeasurement(IperfClientProtocol iperf3Protocol, List<String> iperf3Output, IperfRole iperfRole) {
        int indexOfBitrate = 0;
        int indexOfRetr = 0;
        String resultLine = "";
        // we iterate backwards through the iperf3 output, because results are expected to be at the end
        for (int i = iperf3Output.size() - 1; i >= 0; i--) {
            if (iperf3Output.get(i).contains("[ ID]")) {
                indexOfBitrate = iperf3Output.get(i).indexOf("Bitrate");
                indexOfRetr = iperf3Output.get(i).indexOf("Retr");
                break;
            }
            if (iperf3Output.get(i).contains(iperfRole.toString().toLowerCase())) {
                resultLine = iperf3Output.get(i);
            }
        }
        // extract bitrate, it is located between
        int bitrate = Integer.parseInt(resultLine.substring(indexOfBitrate, indexOfRetr).replaceAll("[^0-9]", ""));
        return new IperfBandwidthMeasurement(iperf3Protocol, bitrate, iperfRole);
    }

    // TODO: validate cmd, means if it is following the desired format

    // refers to both TCP and UDP, no differentiation because of the protocol
    public static boolean isReceiver(String cmd) {
        if (cmd.contains("-s") || cmd.contains("--server")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isTCPSender(String cmd) {
        if (cmd.contains("-c") || cmd.contains("--client")) {
            if (isUDPSender(cmd)) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isUDPSender(String cmd) {
        if (cmd.contains("-u") || cmd.contains("--udp")) {
            return true;
        } else {
            return false;
        }
    }

    public static int extractPortFromIperf3Cmd(String cmd) {
        // find position of -p
        int indexOfPort = cmd.indexOf("-p");
        int indexOfNextCommand = cmd.indexOf("-f");
        return Integer.parseInt(cmd.substring(indexOfPort + 3, indexOfNextCommand - 1));
    }

    public static String extractHostFromIperf3Cmd(String cmd) {
        // find position of -c
        int indexOfHost = cmd.indexOf("-c");
        int indexOfNextCommand;

        if (isUDPSender(cmd)) {
            indexOfNextCommand = cmd.indexOf("-u");
        } else {
            indexOfNextCommand = cmd.indexOf("-p");
        }
        return cmd.substring(indexOfHost + 3, indexOfNextCommand - 1);
    }

    // TODO: validate cmds
}
