package dev.pulceo.pna.model.nping;

import lombok.Data;

@Data
public class NpingClientCmd {

    private final NpingClientProtocol npingClientProtocol;
    private final int port;
    private final int rounds;
    private final String host;

    public String getCmd() {
        if (isUDPClient()) {
            return String.format("/usr/bin/nping --udp -p %s -c %s %s", port, rounds, host);
        } else {
            return String.format("/usr/bin/nping --tcp-connect -p %s -c %s %s", port, rounds, host);
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
