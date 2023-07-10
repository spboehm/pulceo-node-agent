package dev.pulceo.pna;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.model.job.NpingJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.service.DelayService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.Objects;

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
    public void testMeasureUDPDelay() throws DelayServiceException {
        // given
        String sourceHost = "localhost";
        int port = Integer.parseInt(Objects.requireNonNull(this.environment.getProperty("pna.delay.udp.port")));
        NpingJob npingJob = new NpingJob("localhost", "localhost", port, NpingClientProtocol.UDP,15);

        // when
        this.delayService.measureDelay(npingJob);

        // then

    }

    @Test
    public void testMeasureTCPDelay() throws DelayServiceException {
        // given

        // when

        // then
    }

}
