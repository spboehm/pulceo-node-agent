package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.CloudRegistrationException;
import dev.pulceo.pna.model.registration.CloudRegistration;
import dev.pulceo.pna.model.registration.CloudRegistrationRequest;
import dev.pulceo.pna.model.registration.PnaInitToken;
import dev.pulceo.pna.repository.CloudRegistrationRepository;
import dev.pulceo.pna.repository.PnaInitTokenRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = { "pna.delay.tcp.port=6002", "pna.delay.udp.port=6003", "pna.mqtt.client.id=551e8400-e29b-11d4-a716-446655440001"})
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
    public void testNewIntitialCloudRegistrationWithAlreadyExistingCloudRegistrationWithException() {
        // given
        String prmUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String prmEndpoint = "https://pulceo.dev";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";
        CloudRegistrationRequest cloudRegistrationRequest = new CloudRegistrationRequest(prmUUID, prmEndpoint, pnaInitToken);


        // cloud registration available
        when(this.cloudRegistrationRepository.count()).thenReturn(1L);

        // when + then
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
        });
    }

    @Test
    public void testNewIntitialCloudRegistrationWithMalformedInitTokenWithException() {
        // given
        String prmUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String prmEndpoint = "https://pulceo.dev";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2ZclFhVUExb0VpNktGZ296";
        CloudRegistrationRequest cloudRegistrationRequest = new CloudRegistrationRequest(prmUUID, prmEndpoint, pnaInitToken);


        // cloud registration not available
        when(this.cloudRegistrationRepository.count()).thenReturn(0L);

        // when + then
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
        });
    }

    @Test
    public void testNewInitialCloudRegistrationWithNotMatchingPnaInitTokenWithException() {
        // given
        String prmUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String prmEndpoint = "https://pulceo.dev";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VPNktGZ296";
        CloudRegistrationRequest cloudRegistrationRequest = new CloudRegistrationRequest(prmUUID, prmEndpoint, pnaInitToken);

        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        List<PnaInitToken> pnaInitTokens = List.of(new PnaInitToken(this.pnaId, this.pnaInitToken));
        when(this.pnaInitTokenRepository.findAll()).thenReturn(pnaInitTokens);

        // when
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
        });

        // then
        verify(this.pnaInitTokenRepository, new Times(1)).save(any(PnaInitToken.class));
    }

    @Test
    public void testNewInitialCloudRegistrationWithAlreadyInvalidPnaInitTokenWithException() {
        // given
        String prmUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String prmEndpoint = "https://pulceo.dev";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VPNktGZ296";
        CloudRegistrationRequest cloudRegistrationRequest = new CloudRegistrationRequest(prmUUID, prmEndpoint, pnaInitToken);

        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        PnaInitToken invalidPnaInitToken = new PnaInitToken(this.pnaId, this.pnaInitToken);
        invalidPnaInitToken.setValid(false);
        List<PnaInitToken> pnaInitTokens = List.of(invalidPnaInitToken);
        when(this.pnaInitTokenRepository.findAll()).thenReturn(pnaInitTokens);

        // when and then
        assertThrows(CloudRegistrationException.class, () -> {
            this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);
        });
    }

    @Test
    public void testNewInitialCloudRegistrationWithMatchingPnaInitTokenWithSuccess() throws CloudRegistrationException {
        // given
        String prmUUID = "0247fea1-3ca3-401b-8fa2-b6f83a469680";
        String prmEndpoint = "https://pulceo.dev";
        String pnaInitToken = "b0hRUGwxT0hNYnhGbGoyQ2tlQnBGblAxOmdHUHM3MGtRRWNsZVFMSmdZclFhVUExb0VpNktGZ296";
        CloudRegistrationRequest cloudRegistrationRequest = new CloudRegistrationRequest(prmUUID, prmEndpoint, pnaInitToken);

        when(this.cloudRegistrationRepository.count()).thenReturn(0L);
        when(this.pnaInitTokenRepository.count()).thenReturn(1L);
        List<PnaInitToken> pnaInitTokens = List.of(new PnaInitToken(this.pnaId, this.pnaInitToken));
        when(this.pnaInitTokenRepository.findAll()).thenReturn(pnaInitTokens);

        // when
        this.cloudRegistrationService.newInitialCloudRegistration(cloudRegistrationRequest);

        // then
        verify(this.pnaInitTokenRepository, new Times(1)).count();
        verify(this.pnaInitTokenRepository, new Times(1)).findAll();
        verify(this.cloudRegistrationRepository, new Times(1)).save(any(CloudRegistration.class));
    }

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
