package dev.pulceo.pna;

import dev.pulceo.pna.exception.PNAException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Component
public class InitPulceoNodeAgentBean {

    Logger logger = LoggerFactory.getLogger(InitPulceoNodeAgentBean.class);

    @Value("${pna.config.path}")
    private String pnaConfigPath;

    @PostConstruct
    public void init() {
        createDirectoryIfNotExists(pnaConfigPath);

        /* pna_id */
        createFileIfNotExists(pnaConfigPath, "pna_id");
        generatePnaIdIfInvalid(pnaConfigPath + "/pna_id");
        // TODO: propagate pna id to other services

        /* pna_init_token */


    }

    private void createDirectoryIfNotExists(String path) {
        if (!new File(path).exists()) {
            boolean directoryCreated = new File(path).mkdirs();
            if (!directoryCreated) {
                throw new RuntimeException(new PNAException("Startup failed: Could not create directory" + path + "!"));
            }
            logger.info("Created directory: " + path);
        }
        logger.info("Found directory: " + path);
    }

    private void createFileIfNotExists(String path, String filename) {
        if (!new File(path + "/" + filename).exists()) {
            try {
                boolean fileCreated = new File(path + "/" + filename).createNewFile();
                if (!fileCreated) {
                    throw new RuntimeException(new PNAException("Startup failed: Could not create file " + filename + "!"));
                }
                logger.info("Created file: " + path + "/" + filename);
            } catch (IOException e) {
                throw new RuntimeException(new PNAException("Startup failed: Could not create file " + filename + "!", e));
            }
        }
    }

    private void generatePnaIdIfInvalid(String path) {
        try {
            boolean pnaIdIsValid = validatePnaId(readFile(path));
            if (!pnaIdIsValid) {
                logger.warn("PNA id is invalid! Generating new PNA id...");
                generatePnaId(path);
            }
            logger.info("Found valid pna id: " + path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void generatePnaId(String path) throws IOException {
        String generatedPNAId = "pna-" + UUID.randomUUID();
        Files.writeString(Path.of(path), generatedPNAId);
        logger.info("Generated new PNA id: " + generatedPNAId);
        logger.info("Successfully wrote PNA id to file: " + path);
    }

    private String readFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    private boolean validatePnaId (String pnaId) {
        return pnaId.matches("pna-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+");
    }

}
