package dev.pulceo.pna.model.iperf3;

import lombok.Data;

@Data
public class Iperf3ServerProcess {
    private final long pid;
    private final String fname;
    private final String cmd;
}
