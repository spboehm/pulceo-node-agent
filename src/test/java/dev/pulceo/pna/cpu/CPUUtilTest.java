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
                .cores(8)
                .threads(16)
                .MIPS(7203.61f)
                .GFlop(0.0f)
                .frequency(2200.000f)
                .slots(0.0f)
                .build();

        // when
        CPU actualCPUInformation = CPUUtil.extractCPUInformation(lscpuAsList);

        // then
        assertEquals(expectedCPU.getCores(), actualCPUInformation.getCores());
        assertEquals(expectedCPU.getThreads(), actualCPUInformation.getThreads());
        assertEquals(expectedCPU.getMIPS(), actualCPUInformation.getMIPS());
        assertEquals(expectedCPU.getGFlop(), actualCPUInformation.getGFlop());
        assertEquals(expectedCPU.getFrequency(), actualCPUInformation.getFrequency());
        assertEquals(expectedCPU.getSlots(), actualCPUInformation.getSlots());
    }

}
