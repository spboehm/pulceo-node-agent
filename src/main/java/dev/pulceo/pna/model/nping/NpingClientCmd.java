package dev.pulceo.pna.model.nping;

import lombok.Data;

@Data
public class NpingClientCmd {

    private final NpingClientProtocol npingClientProtocol;
    private final int port;
    private final int rounds;
    private final String host;
    private final String iface;

    public String getCmd() {
        if (isUDPClient()) {
            return String.format("/usr/bin/nping --udp -p %s -c %s %s -e %s", port, rounds, host, iface);
        } else {
            return String.format("/usr/bin/nping --tcp-connect -p %s -c %s %s -e %s", port, rounds, host, iface);
        }
    }

    public boolean isUDPClient() {
        if (this.npingClientProtocol == NpingClientProtocol.UDP) {
            return true;
        } else {
            return false;
        }
    }

}
