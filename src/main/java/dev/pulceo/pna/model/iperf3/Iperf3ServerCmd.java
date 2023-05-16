package dev.pulceo.pna.model.iperf3;

import lombok.Data;

import java.util.UUID;

@Data
public class Iperf3ServerCmd {

    private final String uuid = UUID.randomUUID().toString();
    private final int port;
    private final String format = "m";

    public String getCmd() {
        return String.format("/bin/iperf3 -s -p %s -f %s", port, format);
    }
}
