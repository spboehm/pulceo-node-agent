package dev.pulceo.pna.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.CPU;

import java.io.IOException;
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

        // if no cpu frequency is available, fallback to /proc/cpuinfo
        if (minFrequency == 0 || maxFrequency == 0) {
            try {
                Process lscpuProcess = new ProcessBuilder("cat", "/proc/cpuinfo").start();
                lscpuProcess.waitFor();
                float frequency = extractFrequencyFromProcCpuInfo(ProcessUtils.readProcessOutput(lscpuProcess.getInputStream()));
                minFrequency = frequency;
                maxFrequency = frequency;
            } catch (IOException | ProcessException | InterruptedException e) {
                System.err.println("Could not obtain CPU frequency from /proc/cpuinfo");
            }

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
                .shares(threadsPerCore * coresPerSocket * sockets * 1000)
                .build();
    }

    public static float extractFrequencyFromProcCpuInfo(List<String> procCpuInfoAsString) throws JsonProcessingException {
        for (String line : procCpuInfoAsString) {
            if (line.contains("cpu MHz")) {
                String[] split = line.split(":");
                return Float.parseFloat(split[1].trim());
            }
        }
        return 0.0f;
    }

}
