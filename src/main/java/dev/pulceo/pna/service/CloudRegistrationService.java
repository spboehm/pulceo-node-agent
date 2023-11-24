package dev.pulceo.pna.service;

import dev.pulceo.pna.model.registration.CloudRegistration;
import org.springframework.stereotype.Service;

@Service
public class CloudRegistrationService {

    public void registerCloud(CloudRegistration cloudRegistration) {
        // TODO: validate token



    }

    public void registerPnaInitToken(String token) {

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
