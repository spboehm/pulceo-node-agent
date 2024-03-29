package dev.pulceo.pna.cpu;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.CPU;
import dev.pulceo.pna.util.CPUUtil;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CPUUtilTest {

    @Test
    public void testParseCPUInformationOnPremiseMachine() throws ProcessException, IOException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/cpu/lscpu-desktop.txt");
        List<String> lscpuAsList;
        try(InputStream inputStream = new FileInputStream(file)) {
            lscpuAsList = ProcessUtils.readProcessOutput(inputStream);
        }
        CPU expectedCPU = CPU.builder()
                .modelName("AMD Ryzen 7 3700X 8-Core Processor")
                .cores(8)
                .threads(16)
                .bogoMIPS(7203.61f)
                .MIPS(7203.61f)
                .GFlop(0.0f)
                .minimalFrequency(2200.0000f)
                .maximalFrequency(4426.1709f)
                .averageFrequency(3313.0854f)
                .slots(0.0f)
                .shares(16000)
                .build();

        // when
        CPU actualCPUInformation = CPUUtil.extractCPUInformation(lscpuAsList);

        // then
        assertEquals(expectedCPU.getModelName(), actualCPUInformation.getModelName());
        assertEquals(expectedCPU.getCores(), actualCPUInformation.getCores());
        assertEquals(expectedCPU.getThreads(), actualCPUInformation.getThreads());
        assertEquals(expectedCPU.getBogoMIPS(), actualCPUInformation.getBogoMIPS());
        assertEquals(expectedCPU.getMIPS(), actualCPUInformation.getMIPS());
        assertEquals(expectedCPU.getGFlop(), actualCPUInformation.getGFlop());
        assertEquals(expectedCPU.getMinimalFrequency(), actualCPUInformation.getMinimalFrequency());
        assertEquals(expectedCPU.getMaximalFrequency(), actualCPUInformation.getMaximalFrequency());
        assertEquals(expectedCPU.getAverageFrequency(), actualCPUInformation.getAverageFrequency());
        assertEquals(expectedCPU.getSlots(), actualCPUInformation.getSlots());
    }

    @Test
    public void testParseCPUInformationAzureCloudMachine() throws ProcessException, IOException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/cpu/lscpu-azure-b1s.txt");
        List<String> lscpuAsList;
        try(InputStream inputStream = new FileInputStream(file)) {
            lscpuAsList = ProcessUtils.readProcessOutput(inputStream);
        }
        CPU expectedCPU = CPU.builder()
                .modelName("Intel(R) Xeon(R) Platinum 8272CL CPU @ 2.60GHz")
                .cores(1)
                .threads(1)
                .bogoMIPS(5187.81f)
                .MIPS(5187.81f)
                .GFlop(0.0f)
                .minimalFrequency(2593.905f)
                .maximalFrequency(2593.905f)
                .averageFrequency(2593.905f)
                .slots(0.0f)
                .shares(1000)
                .build();

        // when
        CPU actualCPUInformation = CPUUtil.extractCPUInformation(lscpuAsList);

        // then
        assertEquals(expectedCPU.getModelName(), actualCPUInformation.getModelName());
        assertEquals(expectedCPU.getCores(), actualCPUInformation.getCores());
        assertEquals(expectedCPU.getThreads(), actualCPUInformation.getThreads());
        assertEquals(expectedCPU.getBogoMIPS(), actualCPUInformation.getBogoMIPS());
        assertEquals(expectedCPU.getMIPS(), actualCPUInformation.getMIPS());
        assertEquals(expectedCPU.getGFlop(), actualCPUInformation.getGFlop());
        assertEquals(expectedCPU.getMinimalFrequency(), actualCPUInformation.getMinimalFrequency());
        assertEquals(expectedCPU.getMaximalFrequency(), actualCPUInformation.getMaximalFrequency());
        assertEquals(expectedCPU.getAverageFrequency(), actualCPUInformation.getAverageFrequency());
        assertEquals(expectedCPU.getSlots(), actualCPUInformation.getSlots());
    }

    @Test
    public void testParseCPUInformationNotebookMachine() throws ProcessException, IOException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/cpu/lscpu-notebook.txt");
        List<String> lscpuAsList;
        try(InputStream inputStream = new FileInputStream(file)) {
            lscpuAsList = ProcessUtils.readProcessOutput(inputStream);
        }
        CPU expectedCPU = CPU.builder()
                .modelName("12th Gen Intel(R) Core(TM) i7-1260P")
                .cores(12)
                .threads(24)
                .bogoMIPS(4993.00f)
                .MIPS(4993.00f)
                .GFlop(0.0f)
                .minimalFrequency(400.0000f)
                .maximalFrequency(4700.0000f)
                .averageFrequency(2550.0000f)
                .slots(0.0f)
                .shares(24000)
                .build();

        // when
        CPU actualCPUInformation = CPUUtil.extractCPUInformation(lscpuAsList);

        // then
        assertEquals(expectedCPU.getModelName(), actualCPUInformation.getModelName());
        assertEquals(expectedCPU.getCores(), actualCPUInformation.getCores());
        assertEquals(expectedCPU.getThreads(), actualCPUInformation.getThreads());
        assertEquals(expectedCPU.getBogoMIPS(), actualCPUInformation.getBogoMIPS());
        assertEquals(expectedCPU.getMIPS(), actualCPUInformation.getMIPS());
        assertEquals(expectedCPU.getGFlop(), actualCPUInformation.getGFlop());
        assertEquals(expectedCPU.getMinimalFrequency(), actualCPUInformation.getMinimalFrequency());
        assertEquals(expectedCPU.getMaximalFrequency(), actualCPUInformation.getMaximalFrequency());
        assertEquals(expectedCPU.getAverageFrequency(), actualCPUInformation.getAverageFrequency());
        assertEquals(expectedCPU.getSlots(), actualCPUInformation.getSlots());
    }

    @Test
    public void testParseCPUInformationProcCpuInfo() throws IOException, ProcessException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/cpu/proc-cpu-info.txt");
        List<String> procCpuInfoAsList;
        try(InputStream inputStream = new FileInputStream(file)) {
            procCpuInfoAsList = ProcessUtils.readProcessOutput(inputStream);
        }
        float expectedFrequency = 2593.904f;

        // when
        float actualFrequency = CPUUtil.extractFrequencyFromProcCpuInfo(procCpuInfoAsList);

        // then
        assertEquals(expectedFrequency, actualFrequency);

    }

}
