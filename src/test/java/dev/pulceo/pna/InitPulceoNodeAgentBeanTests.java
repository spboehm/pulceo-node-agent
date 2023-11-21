package dev.pulceo.pna;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class InitPulceoNodeAgentBeanTests {

    @Autowired
    private InitPulceoNodeAgentBean initPulceoNodeAgentBean;

    @Value("${pna.config.path}")
    private String pnaConfigPath;

    private void deletePnaConfigDirectory() {
        File pnaConfigDirectory = new File(pnaConfigPath);
        if (pnaConfigDirectory.exists()) {
            for (File f : pnaConfigDirectory.listFiles()) {
                f.delete();
            }
            pnaConfigDirectory.delete();
        }
    }

    @Test
    public void testInitWithNotExistingConfigDirectory() {
        // given
        deletePnaConfigDirectory();

        // when
        initPulceoNodeAgentBean.init();

        // then
        assertTrue(new File(pnaConfigPath).isDirectory());
    }

    @Test
    public void testInitWithNotExistingPnaId() throws IOException {
        // given
        deletePnaConfigDirectory();
        boolean configDirectoryCreated = new File(pnaConfigPath).mkdirs();
        if(!configDirectoryCreated) {
            throw new RuntimeException("Could not create config directory!");
        }

        // when
        initPulceoNodeAgentBean.init();

        // then
        String filePath = pnaConfigPath + "/" + "pna_id";
        assertTrue(new File(filePath).isFile());
        assertTrue(Files.readString(Path.of(filePath), StandardCharsets.UTF_8).matches("pna-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+"));
    }

    @Test
    public void testInitWithExistingPnaId() throws IOException {
        // given
        deletePnaConfigDirectory();
        boolean directoryCreated = new File(pnaConfigPath).mkdirs();
        String pnaIdPath = pnaConfigPath + "/" + "pna_id";
        boolean fileCreated = new File(pnaIdPath).createNewFile();
        if (!directoryCreated && !fileCreated) {
            throw new RuntimeException("Could not create config directory and file for pna id!");
        }
        String generatedPNAId = "pna-" + UUID.randomUUID();
        Files.writeString(Path.of(pnaIdPath), generatedPNAId);

        // when
        initPulceoNodeAgentBean.init();

        // then
        assertEquals(generatedPNAId, Files.readString(Path.of(pnaIdPath), StandardCharsets.UTF_8));
    }

}
