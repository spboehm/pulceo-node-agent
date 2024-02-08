package dev.pulceo.pna.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.model.node.CPU;

import java.util.List;

public class CPUUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static CPU extractCPUInformation(List<String> lsCPUAsString) throws JsonProcessingException {
        // only used for calculation
        String modelName = "";
        int sockets = 0;
        int coresPerSocket = 0;
        int threadsPerCore = 0;

        float bogoMIPS = 0;
        float MIPS = 0;
        float GFlop = 0;
        float minFrequency = 0;
        float maxFrequency = 0;
        float avgFrequency = 0;
        float slots = 0;

        for (String line : lsCPUAsString) {
            if (line.contains("Model name:")) {
                String[] split = line.split(":");
                modelName = split[1].trim();
            } else if (line.contains("Socket(s):")) {
                String[] split = line.split(":");
                sockets = Integer.parseInt(split[1].trim());
            } else if (line.contains("Core(s) per socket:")) {
                String[] split = line.split(":");
                coresPerSocket = Integer.parseInt(split[1].trim());
            } else if (line.contains("Thread(s) per core:")) {
                String[] split = line.split(":");
                threadsPerCore = Integer.parseInt(split[1].trim());
            } else if (line.contains("BogoMIPS:")) {
                String[] split = line.split(":");
                bogoMIPS = Float.parseFloat(split[1].trim());
            } else if (line.contains("CPU min MHz:")) {
                String[] split = line.split(":");
                minFrequency = Float.parseFloat(split[1].trim());
            } else if (line.contains("CPU max MHz:")) {
                String[] split = line.split(":");
                maxFrequency = Float.parseFloat(split[1].trim());
            } else if (line.contains("CPU MHz:")) {
                String[] split = line.split(":");
                avgFrequency = Float.parseFloat(split[1].trim());
            }
        }

        // if no min or max frequency is available
        if (minFrequency == 0 || maxFrequency == 0) {
            minFrequency = avgFrequency;
            maxFrequency = avgFrequency;
        }

        return CPU.builder()
                .modelName(modelName)
                .cores(coresPerSocket * sockets)
                .threads(threadsPerCore * coresPerSocket * sockets)
                .bogoMIPS(bogoMIPS)
                .MIPS(bogoMIPS)
                .GFlop(GFlop)
                .minimalFrequency(minFrequency)
                .maximalFrequency(maxFrequency)
                .averageFrequency((maxFrequency + minFrequency) / 2)
                .slots(slots)
                .build();
    }

}
