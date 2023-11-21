package dev.pulceo.pna.service;

import org.springframework.stereotype.Service;

@Service
public class CloudRegistrationService {

    public void registerPnaInitToken(String token) {
        // TODO: validate token
        // TODO: generate new persistent API token
        // TODO: store new persistent API token together with CloudRegistration
    }



    private boolean validateToken(String token) {
        return true;
    }

    private String generatePnaToken() {
        return "";
    }

}
