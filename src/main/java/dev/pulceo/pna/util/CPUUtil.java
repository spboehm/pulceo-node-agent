package dev.pulceo.pna.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.model.node.CPU;

import java.util.List;

public class CPUUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CPU extractCPUInformation(List<String> lsCPUAsString) throws JsonProcessingException {
        int cores = 0;
        int threads = 0;
        float MIPS = 0;
        float GFlop = 0;
        float frequency = 0;
        float slots = 0;

        for (String line : lsCPUAsString) {
            if (line.startsWith("CPU(s):")) {
                String[] split = line.split(":");
                threads = Integer.parseInt(split[1].trim());
            }
            if (line.contains("Core(s) per socket:")) {
                String[] split = line.split(":");
                cores = Integer.parseInt(split[1].trim());
            }
            if (line.contains("MIPS:")) {
                String[] split = line.split(":");
                MIPS = Float.parseFloat(split[1].trim());
            } else if (line.contains("CPU min MHz:")) {
                String[] split = line.split(":");
                frequency = Float.parseFloat(split[1].trim());
            }
        }
        return CPU.builder()
                .cores(cores)
                .threads(threads)
                .MIPS(MIPS)
                .GFlop(GFlop)
                .frequency(frequency)
                .slots(slots)
                .build();
    }

    public int extractMIPS(List<String> cpuInfo) {
        return 0;
    }


}
