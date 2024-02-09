package dev.pulceo.pna.model.iperf;

import lombok.Data;

@Data
public class IperfServerCmd {

    private final int port;
    private final String format = "m";
    private final String bind;

    public String getCmd() {
        return String.format("/usr/bin/iperf3 -s -p %s -f %s --bind %s", port, format, bind);
    }
}
