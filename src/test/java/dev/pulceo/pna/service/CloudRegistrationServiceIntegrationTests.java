package dev.pulceo.pna.service;

import dev.pulceo.pna.model.registration.PnaInitToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CloudRegistrationServiceIntegrationTests {


    @Autowired
    CloudRegistrationService cloudRegistrationService;

    @Test
    public void testRegisterPnaInitTokenWithSuccess() {
        // given
        String expectedPnaUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String expectedToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";

        // when
        PnaInitToken actualPnaInitToken = this.cloudRegistrationService.registerPnaInitToken(expectedPnaUUID, expectedToken);

        // then
        assert(actualPnaInitToken.getPnaUUID().equals(expectedPnaUUID));
        assert(actualPnaInitToken.getToken().equals(expectedToken));
        assertTrue(actualPnaInitToken.isValid());
    }

}
