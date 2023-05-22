package dev.pulceo.pna.model.iperf3;

import lombok.Data;

@Data
public class IperfServerCmd {

    private final int port;
    private final String format = "m";

    public String getCmd() {
        return String.format("/bin/iperf3 -s -p %s -f %s", port, format);
    }
}
