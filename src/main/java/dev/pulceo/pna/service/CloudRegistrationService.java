package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.CloudRegistrationException;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
import dev.pulceo.pna.model.registration.PnaInitToken;
import dev.pulceo.pna.repository.CloudRegistrationRepository;
import dev.pulceo.pna.repository.PnaInitTokenRepository;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Service
public class CloudRegistrationService {

    private final Logger logger = LoggerFactory.getLogger(CloudRegistrationService.class);

    private final PnaInitTokenRepository pnaInitTokenRepository;
    private final CloudRegistrationRepository cloudRegistrationRepository;
    private final NodeService nodeService;

    @Autowired
    public CloudRegistrationService(NodeService nodeService, PnaInitTokenRepository pnaInitTokenRepository, CloudRegistrationRepository cloudRegistrationRepository) {
        this.nodeService = nodeService;
        this.pnaInitTokenRepository = pnaInitTokenRepository;
        this.cloudRegistrationRepository = cloudRegistrationRepository;
    }

    @Value("${pna.uuid}")
    private String pnaId;

    @Value("${pna.init.token}")
    private String pnaInitToken;

    // invoked by prm
    public CloudRegistration newInitialCloudRegistration(CloudRegistrationRequest cloudRegistrationRequest) throws CloudRegistrationException {

        if (isCloudRegistrationExisting()) {
            throw new CloudRegistrationException("A valid cloud registration already exists!");
        }
        /* Maybe remove because already covered by bean validation, except token validation */
        // TODO: redundant bean validation
        // validate prmUUID
        if (!isValidUUID(cloudRegistrationRequest.getPrmUUID())) {
            throw new CloudRegistrationException("prmUUID is not a valid UUID!");
        };

        // validate prmEndpoint
        // TODO: redundant bean validation
        if (!isValidEndpoint(cloudRegistrationRequest.getPrmEndpoint())) {
            throw new CloudRegistrationException("prmEndpoint is not a valid endpoint!");
        };

        // validate pnaInitToken
        // TODO: consider leaving this because it is checking further logic
        if (!isValidPnaInitToken(cloudRegistrationRequest.getPnaInitToken())) {
            throw new CloudRegistrationException("pnaInitToken is not a valid token!");
        };

        /* case everything good */
        if (isPnaInitTokenExistingInDB()) {
            PnaInitToken pnaInitToken = this.pnaInitTokenRepository.findAll().iterator().next();

            if (!pnaInitToken.isValid()) {
                throw new CloudRegistrationException("pnaInitToken is not valid!");
            }

            if (!pnaInitToken.getToken().equals(cloudRegistrationRequest.getPnaInitToken())) {
                throw new CloudRegistrationException("pnaInitToken does not match!");
            }

            String prmUUID = cloudRegistrationRequest.getPrmUUID();
            String prmEndpoint = cloudRegistrationRequest.getPrmEndpoint();
            // TODO: hash token
            String pnaToken = generatePnaToken();
            // Obtain localnode
            Optional<Node> localNode = this.nodeService.readLocalNode();
            if (localNode.isEmpty()) {
                throw new CloudRegistrationException("Local node does not exist!");
            }
            // TODO: replace with pnaInitToken.getToken() with pnaToken in later use cases
            return this.cloudRegistrationRepository.save(new CloudRegistration(localNode.get().getUuid().toString(), this.pnaId, prmUUID, prmEndpoint, pnaInitToken.getToken()));
        } else {
            throw new CloudRegistrationException("pnaInitToken does not exist!");
        }
    }

    // TODO: maybe move this to a separate service
    // only called during startup and for issuing an initial token
    @PostConstruct
    public void initPnaInitToken() throws CloudRegistrationException {
        // TODO: check exception handling for @Postconstruct
        // first check if there is already a valid cloud registration, if yes, abort
        if (isCloudRegistrationExisting()) {
            logger.info("A valid cloud registration already exists, skipping generation...");
            return;
        }

        if (isPnaInitTokenExistingInDB()) {
            logger.info("A valid pna init token already exists in DB, skipping generation...");
            return;
        }

        if (isValidPnaInitTokenExistingInAppProperties()) {
           // continue here with registration and write to DB
            logger.info("Found valid pna init token in application.properties, writing to DB...");
            this.pnaInitTokenRepository.save(new PnaInitToken(this.pnaId, this.pnaInitToken));
            return;
        }
        // otherwise generate a new token and write to DB
        logger.info("Generating new pna init token and writing to DB...");
        this.pnaInitTokenRepository.save(new PnaInitToken(this.pnaId, generatePnaToken()));
    }

    private boolean isPnaInitTokenExistingInDB() {
        return this.pnaInitTokenRepository.count() > 0;
    }

    private boolean isValidPnaInitTokenExistingInAppProperties() {
        Objects.requireNonNull(this.pnaInitToken, "pnaInitToken must not be null!");
        return isValidPnaInitToken(this.pnaInitToken);
    }

    private boolean isCloudRegistrationExisting() {
        return this.cloudRegistrationRepository.count() > 0;
    }

    private boolean isValidUUID(String pnaUUID) {
        Objects.requireNonNull(pnaUUID, "pnaUUID must not be null!");
        return pnaUUID.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }

    private boolean isValidEndpoint(String endpoint) {
        Objects.requireNonNull(endpoint, "endpoint must not be null!");
        // TODO: add additional validation
        return true;
    }

    private boolean isValidPnaInitToken(String base64EncodedInitToken) {
        Objects.requireNonNull(base64EncodedInitToken, "base64EncodedInitToken must not be null!");
        if (base64EncodedInitToken.length() < 76) {
            return false;
        }
        byte[] bytesOfInitToken = Base64.getDecoder().decode(base64EncodedInitToken);
        return new String(bytesOfInitToken).matches("[a-zA-Z0-9]{24}:[a-zA-Z0-9]{32}");
    }

    private String generatePnaToken() {
        String initTokenId = generateSecureRandomString(24);
        String initTokenSecret = generateSecureRandomString(32);
        return Base64.getEncoder().encodeToString((initTokenId + ":" + initTokenSecret).getBytes());
    }

    private String generateSecureRandomString(int length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }

}
