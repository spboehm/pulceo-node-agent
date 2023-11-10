package dev.pulceo.pna.model.iperf;

import lombok.Data;

@Data
public class IperfServerCmd {

    private final int port;
    private final String format = "m";
    private final String bindDev;

    public String getCmd() {
        return String.format("/bin/iperf3 -s -p %s -f %s --bind-dev %s", port, format, bindDev);
    }
}
