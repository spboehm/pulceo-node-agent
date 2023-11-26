package dev.pulceo.pna.service;

import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
import dev.pulceo.pna.model.registration.PnaInitToken;
import dev.pulceo.pna.repository.PnaInitTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CloudRegistrationService {

    @Autowired
    private PnaInitTokenRepository pnaInitTokenRepository;

    public CloudRegistration newInitialCloudRegistration(CloudRegistrationRequest cloudRegistrationRequest) {
        // TODO: validate token


        return new CloudRegistration();
    }

    // only called during startup and for issuing an initial token
    public PnaInitToken registerPnaInitToken(String pnaUUID, String token) {
        // TODO: validate prmUUID
        // TODO: validate pnaInitToken

        // TODO: check if a token with valid true exists
        PnaInitToken pnaInitToken = new PnaInitToken(pnaUUID, token);

        // TODO: generate new persistent API token
        // TODO: store new persistent API token together with CloudRegistration
        return this.pnaInitTokenRepository.save(pnaInitToken);
    }



    private boolean validateToken(String token) {
        return true;
    }

    private String generatePnaToken() {
        return "";
    }

}
