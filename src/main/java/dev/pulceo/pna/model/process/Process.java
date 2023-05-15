package dev.pulceo.pna.model.process;

import lombok.Data;

@Data
public class Process {

    private final long pid;
    private final String fname;
    private final String cmd;

}
