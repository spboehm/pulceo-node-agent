package dev.pulceo.pna.model.iperf3;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class Iperf3ServerCmd {

    private final int port;
    private final String format = "m";

    public String getCmd() {
        return String.format("/bin/iperf3 -s -p %s -f %s", port, format);
    }
}
