package dev.pulceo.pna;

import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.service.PingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PingServiceTests {

    @Autowired
    PingService pingService;

    @Value("${pna.local.address}")
    private String localAddress;

    @BeforeEach
    @AfterEach
    public void killAllPingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "ping").start();
        p.waitFor();
    }

    private static void startPingInstance(String destinationHost) throws IOException, InterruptedException {
        Process pingInstance = new ProcessBuilder("/usr/bin/ping", "-4", "-s", "66", "-I", "lo", destinationHost).start();
        while (!pingInstance.isAlive()) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCheckForRunningPingInstance() throws IOException, InterruptedException, PingServiceException {
        // given
        String destinationHost = localAddress;
        startPingInstance(destinationHost);

        // when
        boolean pingInstanceRunning = pingService.checkForRunningPingInstance(destinationHost);

        // then
        assertTrue(pingInstanceRunning);

    }

    @Test
    public void testMeasurePingDelay() throws PingServiceException {
        // given
        PingRequest pingRequest = new PingRequest(localAddress, localAddress, IPVersion.IPv4, 1, 66, "lo");

        // when
        PingResult actualPingResult = pingService.measureRoundTripTime(pingRequest);

        // then
        assertEquals(localAddress, actualPingResult.getSourceHost());
        assertEquals(localAddress, actualPingResult.getSourceHost());
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
