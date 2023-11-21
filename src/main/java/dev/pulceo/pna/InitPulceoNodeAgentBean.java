package dev.pulceo.pna;

import dev.pulceo.pna.exception.PNAException;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;

@Component
public class InitPulceoNodeAgentBean {

    private final Logger logger = LoggerFactory.getLogger(InitPulceoNodeAgentBean.class);

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
        // TODO: only generate if device has not been connected to the cloud
        // design decision: if there was no connection established to the cloud, pna_init_token will reset on every startup
        createFileIfNotExists(pnaConfigPath, "pna_init_token");
        generatePnaInitTokenIfInvalid(pnaConfigPath + "/pna_init_token");
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

    private void generatePnaInitTokenIfInvalid(String path) {
        try {
            boolean pnainitTokenIsValid = validatePnaInitToken(readFile(path));
            if (!pnainitTokenIsValid) {
                logger.warn("PNA init token is invalid! Generating new PNA init token...");
                generatePnaInitToken(path);
            } else {
                logger.info("Found valid pna init token: " + path + readFile(path));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validatePnaInitToken(String Base64EncodedInitToken) {
        byte[] bytesOfInitToken = Base64.getDecoder().decode(Base64EncodedInitToken);
        return new String(bytesOfInitToken).matches("[a-zA-Z0-9]{24}:[a-zA-Z0-9]{32}");
    }

    private void generatePnaId(String path) throws IOException {
        String generatedPNAId = "pna-" + UUID.randomUUID();
        Files.writeString(Path.of(path), generatedPNAId);
        logger.info("Generated new PNA id: " + generatedPNAId);
        logger.info("Successfully wrote PNA id to file: " + path);
    }

    private void generatePnaInitToken(String path) throws IOException {
        String initTokenId = generateSecureRandomString(24);
        String initTokenSecret = generateSecureRandomString(32);
        String initToken = initTokenId + ":" + initTokenSecret;
        String base64EncodedInitToken = Base64.getEncoder().encodeToString(initToken.getBytes());
        Files.write(Path.of(path), base64EncodedInitToken.getBytes());
        logger.info("Generated new PNA init token: " + base64EncodedInitToken);
    }

    private String generateSecureRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

    private String readFile(String filePath) throws IOException {
        return Files.readString(Path.of(filePath), StandardCharsets.UTF_8);
    }

    private boolean validatePnaId(String pnaId) {
        return pnaId.matches("pna-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+");
    }


}
