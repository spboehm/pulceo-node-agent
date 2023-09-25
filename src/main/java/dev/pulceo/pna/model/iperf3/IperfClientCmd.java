package dev.pulceo.pna.model.iperf3;

import lombok.Data;

@Data
public class IperfClientCmd {
    private final String host;
    private final int port;
    private final int time;
    private final IperfClientProtocol iperf3Protocol;
    private final String format = "m";

    public String getCmd() {
        if (isUDPSender()) {
            return String.format("/bin/iperf3 -c %s -u -p %s -t %s -f %s", host, port, time, format);
        } else {
            return String.format("/bin/iperf3 -c %s -p %s -t %s -f %s", host, port, time, format);
        }

    }

    public boolean isUDPSender() {
        if (this.iperf3Protocol == IperfClientProtocol.UDP) {
            return true;
        }
            return false;
    }
}
