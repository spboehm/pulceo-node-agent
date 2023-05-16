package dev.pulceo.pna.model.iperf3;

import lombok.Data;

import java.util.UUID;

@Data
public class Iperf3ClientCmd {

    private final String uuid = UUID.randomUUID().toString();
    private final String host;
    private final int port;
    private final Iperf3ClientProtocol iperf3Protocol;
    private final String format = "m";

    public String getCmd() {
        if (isUDPSender()) {
            return String.format("/bin/iperf3 -c %s -u -p %s -f %s", host, port, format);
        } else {
            return String.format("/bin/iperf3 -c %s -p %s -f %s", host, port, format);
        }

    }

    public boolean isUDPSender() {
        if (this.iperf3Protocol == Iperf3ClientProtocol.UDP) {
            return true;
        }
            return false;
    }

}
