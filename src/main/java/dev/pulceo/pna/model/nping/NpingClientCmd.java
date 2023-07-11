package dev.pulceo.pna.model.nping;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class NpingClientCmd {

    private final NpingClientProtocol npingClientProtocol;
    private final int port;
    private final int rounds;
    private final String host;
    private final String iface;

    public String getCmd() {
        if (isUDPClient()) {
            return String.format("/usr/bin/nping -4 --udp -c %s --dest-ip %s -p %s -e %s", rounds, host, port, iface);
        } else {
            return String.format("/usr/bin/nping -4 --tcp-connect -c %s --dest-ip %s -p %s -e %s", rounds, host, port, iface);
        }
    }

    public boolean isUDPClient() {
        if (this.npingClientProtocol == NpingClientProtocol.UDP) {
            return true;
        } else {
            return false;
        }
    }

    public List<String> getNpingCommandAsList () {
        return Arrays.asList(this.getCmd().split(" "));
    }

}
