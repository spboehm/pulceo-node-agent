package dev.pulceo.pna.service;

import dev.pulceo.pna.InitPulceoNodeAgentBean;
import dev.pulceo.pna.exception.CloudRegistrationException;
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

@Service
public class CloudRegistrationService {

    private final Logger logger = LoggerFactory.getLogger(InitPulceoNodeAgentBean.class);

    @Autowired
    private PnaInitTokenRepository pnaInitTokenRepository;

    @Autowired
    private CloudRegistrationRepository cloudRegistrationRepository;

    @Value("${pna.id}")
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
        if (!isValidUUID(cloudRegistrationRequest.prmUUID())) {
            throw new CloudRegistrationException("prmUUID is not a valid UUID!");
        };

        // validate prmEndpoint
        // TODO: redundant bean validation
        if (!isValidEndpoint(cloudRegistrationRequest.prmEndpoint())) {
            throw new CloudRegistrationException("prmEndpoint is not a valid endpoint!");
        };

        // validate pnaInitToken
        // TODO: consider leaving this because it is checking further logic
        if (!isValidPnaInitToken(cloudRegistrationRequest.pnaInitToken())) {
            throw new CloudRegistrationException("pnaInitToken is not a valid token!");
        };

        /* case everything good */
        if (isPnaInitTokenExistingInDB()) {
            PnaInitToken pnaInitToken = this.pnaInitTokenRepository.findAll().iterator().next();

            if (!pnaInitToken.isValid()) {
                throw new CloudRegistrationException("pnaInitToken is not valid!");
            }

            if (!pnaInitToken.getToken().equals(cloudRegistrationRequest.pnaInitToken())) {
                throw new CloudRegistrationException("pnaInitToken does not match!");
            }

            String prmUUID = cloudRegistrationRequest.prmUUID();
            String prmEndpoint = cloudRegistrationRequest.prmEndpoint();
            // TODO: hash token
            String pnaToken = generatePnaToken();
            return this.cloudRegistrationRepository.save(new CloudRegistration(prmUUID, prmEndpoint, pnaToken));
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
            throw new CloudRegistrationException("A valid cloud registration already exists!");
        }

        if (isPnaInitTokenExistingInDB()) {
            throw new CloudRegistrationException("A pna init token already exists!");
        }

        if (isValidPnaInitTokenExistingInAppProperties()) {
           // continue here with registration and write to DB
            this.pnaInitTokenRepository.save(new PnaInitToken(this.pnaId, this.pnaInitToken));
            return;
        }
        // otherwise generate a new token and write to DB
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
        return pnaUUID.matches("[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+-[a-fA-F0-9]+");
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
