package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.CloudRegistrationException;
import dev.pulceo.pna.model.registration.PnaInitToken;
import dev.pulceo.pna.repository.CloudRegistrationRepository;
import dev.pulceo.pna.repository.PnaInitTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = { "pna.delay.tcp.port=6002", "pna.delay.udp.port=6003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440000"})
public class CloudRegistrationServiceUnitTests {

    @MockBean
    PnaInitTokenRepository pnaInitTokenRepository;

    @MockBean
    CloudRegistrationRepository cloudRegistrationRepository;

    @Autowired
    @InjectMocks
    CloudRegistrationService cloudRegistrationService;

    private final String pnaId = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
    private final String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";


    @Test
    public void testInitPnaTokenWithAlreadyExistingCloudRegistrationWithException() {
        // given
        // cloud registration available
        when(this.cloudRegistrationRepository.count()).thenReturn(1L);

        // when + then
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.initPnaInitToken();
        });
    }

    @Test
    public void testInitPnaTokenWithAlreadyExistingPnaInitTokenInDBWithException() {
        // given
        // no cloud registration available
        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        // pna init token available in DB
        when(this.pnaInitTokenRepository.count()).thenReturn(1L);

        // when + then
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.initPnaInitToken();
        });
    }

    // TODO:
    /* Test case with invalid pna init token obtained from application.properties, please see CloudRegistrationServiceWithInvalidPnaInitTokenUnitTests */

    @Test
    public void testPnaInitTokenWithGeneratedPnaInitTokenWithSuccess() throws CloudRegistrationException {
        // given
        // no cloud registration available
        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        // no pna init token available in DB
        when(this.pnaInitTokenRepository.count()).thenReturn(0L);

        // when
        this.cloudRegistrationService.initPnaInitToken();

        // then
        verify(this.pnaInitTokenRepository, new Times(1)).save(any(PnaInitToken.class));
    }

    @Test
    public void testInitPnaTokenWithGeneratedPnaInitTokenWithSuccess() throws CloudRegistrationException {
        // given
        // no cloud registration available
        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        // no pna init token available in DB
        when(this.pnaInitTokenRepository.count()).thenReturn(0L);
        // application.properties contains an invalid token with ""

        // when
        this.cloudRegistrationService.initPnaInitToken();

        // then
        verify(this.pnaInitTokenRepository, new Times(1)).save(any(PnaInitToken.class));
    }

}
