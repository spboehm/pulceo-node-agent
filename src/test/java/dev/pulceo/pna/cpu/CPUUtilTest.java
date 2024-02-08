package dev.pulceo.pna.cpu;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.CPU;
import dev.pulceo.pna.util.CPUUtil;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
                .minFrequency(2200.0000f)
                .maxFrequency(4426.1709f)
                .slots(0.0f)
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
        assertEquals(expectedCPU.getMinFrequency(), actualCPUInformation.getMinFrequency());
        assertEquals(expectedCPU.getMaxFrequency(), actualCPUInformation.getMaxFrequency());
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
                .minFrequency(2593.905f)
                .maxFrequency(2593.905f)
                .avgFrequency(2593.905f)
                .slots(0.0f)
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
        assertEquals(expectedCPU.getMinFrequency(), actualCPUInformation.getMinFrequency());
        assertEquals(expectedCPU.getMaxFrequency(), actualCPUInformation.getMaxFrequency());
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
                .minFrequency(400.0000f)
                .maxFrequency(4700.0000f)
                .avgFrequency(2550.0000f)
                .slots(0.0f)
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
        assertEquals(expectedCPU.getMinFrequency(), actualCPUInformation.getMinFrequency());
        assertEquals(expectedCPU.getMaxFrequency(), actualCPUInformation.getMaxFrequency());
        assertEquals(expectedCPU.getSlots(), actualCPUInformation.getSlots());
    }

}
