package dev.pulceo.pna;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPResult;
import dev.pulceo.pna.model.nping.NpingUDPResult;
import dev.pulceo.pna.service.DelayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DelayServiceTests {

    @Autowired
    DelayService delayService;

    @Autowired
    Environment environment;

    @Test
    void contextLoads() {

    }

    @BeforeEach
    @AfterEach
    public void killAllNpingInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "nping").start();
        p.waitFor();
    }

    private static void startNpingUDPInstance(String host, int port, int count) throws IOException, InterruptedException {
        Process npingUDPInstance = new ProcessBuilder("/usr/bin/nping", "-4", "--udp", "-c", String.valueOf(count), "--dest-ip", host ,"-p", String.valueOf(port), "-e" , "eth0", "--data-length", "66").start();
        while (!npingUDPInstance.isAlive()) {
            Thread.sleep(1000);
        }
    }

    @Test
    public void testCheckForRunningNpingUDPInstance() throws IOException, InterruptedException, DelayServiceException {
        // given
        String host = "localhost";
        int port = Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("pna.delay.udp.port")));
        int count = 0;
        startNpingUDPInstance(host, port, count);

        // when
        boolean npingUDPInstanceRunning = this.delayService.checkForRunningNpingInstance(NpingClientProtocol.UDP, host, port);

        // then
        assertTrue(npingUDPInstanceRunning);
    }

    @Test
    public void testMeasureUDPDelay() throws DelayServiceException, InterruptedException {
        // given
        String destinationHost = "localhost";

        // when
        NpingUDPResult npingUDPResult = this.delayService.measureUDPDelay(destinationHost);

        // then
        assertEquals(npingUDPResult.getSourceHost(), "localhost");
        assertEquals(npingUDPResult.getDestinationHost(), destinationHost);
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getAvgRTT() > 0);
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getMinRTT() > 0);
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getMaxRTT() > 0);
        assertEquals(npingUDPResult.getNpingUDPDelayMeasurement().getUdpPacketsSent(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("pna.nping.rounds"))));
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getUdpReceivedPackets() >= 0);
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getUdpPacketsSent() >= 0);
        assertTrue(npingUDPResult.getNpingUDPDelayMeasurement().getUdpLostPacketsRelative() >= 0);
    }

    @Test
    public void testMeasureTCPDelay() throws DelayServiceException {
        // given
        String destinationHost = "localhost";

        // when
        NpingTCPResult npingTCPResult = this.delayService.measureTCPDelay(destinationHost);

        // then
        assertEquals(npingTCPResult.getSourceHost(), "localhost");
        assertEquals(npingTCPResult.getDestinationHost(), destinationHost);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getAvgRTT() > 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getMinRTT() > 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getMaxRTT() > 0);
        assertEquals(npingTCPResult.getNpingTCPDelayMeasurement().getTcpConnectionAttempts(), Integer.parseInt(Objects.requireNonNull(environment.getProperty("pna.nping.rounds"))));
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getTcpSuccessfulConnections() >= 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getTcpFailedConnectionsAbsolute() >= 0);
        assertTrue(npingTCPResult.getNpingTCPDelayMeasurement().getTcpFailedConnectionsRelative() >= 0);

    }

}
