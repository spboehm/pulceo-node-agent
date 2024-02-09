package dev.pulceo.pna.cpu;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.Memory;
import dev.pulceo.pna.util.MemoryUtil;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryUtilTest {

    @Test
    public void testParseMemoryInformationOnPremiseMachine() throws ProcessException, IOException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/memory/output.txt");
        List<String> lsMemoryAsList;
        try(InputStream inputStream = new FileInputStream(file)) {
            lsMemoryAsList = ProcessUtils.readProcessOutput(inputStream);
        }
        Memory expectedMemory = Memory.builder()
                .size(16067148.0f / 1024 / 1024)
                .slots(0)
                .build();

        // when
        Memory actualMemoryInformation = MemoryUtil.extractMemoryInformation(lsMemoryAsList);

        // then
        assertEquals(expectedMemory.getSize(), actualMemoryInformation.getSize());
        assertEquals(expectedMemory.getSlots(), actualMemoryInformation.getSlots());
    }

}
