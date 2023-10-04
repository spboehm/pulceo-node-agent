package dev.pulceo.pna;

import dev.pulceo.pna.exception.PingException;
import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.service.PingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest
public class PingServiceTests {

    @Autowired
    PingService pingService;

    @BeforeEach
    @AfterEach
    public void killAllPingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "ping").start();
        p.waitFor();
    }

    @Test
    public void testMeasurePingDelay() throws PingServiceException, PingException {
        // given
        PingRequest pingRequest = new PingRequest("localhost", "localhost", IPVersion.IPv4, 1, 66, "lo");

        // when
        PingResult actualPingResult = pingService.measureRoundTripTime(pingRequest);

        // then
        assertEquals("localhost", actualPingResult.getSourceHost());
        assertEquals("localhost", actualPingResult.getSourceHost());
        assertEquals(1, actualPingResult.getPingDelayMeasurement().getPacketsTransmitted());
        assertEquals(1, actualPingResult.getPingDelayMeasurement().getPacketsReceived());
        assertEquals(0.0f, actualPingResult.getPingDelayMeasurement().getPacketLoss(), 0.1);
        assertTrue(actualPingResult.getPingDelayMeasurement().getTime() >= 0);
        assertTrue(actualPingResult.getPingDelayMeasurement().getRttMin() > 0.0f);
        assertTrue(actualPingResult.getPingDelayMeasurement().getRttAvg() > 0.0f);
        assertTrue(actualPingResult.getPingDelayMeasurement().getRttMax() > 0.0f);
        assertTrue(actualPingResult.getPingDelayMeasurement().getRttMdev() >= 0.0f);



    }

}
