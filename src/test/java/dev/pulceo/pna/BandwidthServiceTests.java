package dev.pulceo.pna;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
import dev.pulceo.pna.model.iperf3.Iperf3Result;
import dev.pulceo.pna.model.iperf3.Iperf3Role;
import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class BandwidthServiceTests {

    @Autowired
    BandwidthService bandwidthService;

    @Test
    void contextLoads() {
    }

    @BeforeAll
    @AfterAll
    public static void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
    }

    @Test
    public void testCheckForRunningIperf3TCPReceiverInstance() throws IOException, InterruptedException, ProcessException {
        // given
        int port = 5001;
        Process iperf3ProcessOne = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port), "-f m").start();

        while (!iperf3ProcessOne.isAlive()) {
            Thread.sleep(1000);
        }

        // when
        boolean TCPReceiverInstanceRunning = bandwidthService.checkForRunningIperf3Instance(Iperf3Protocol.TCP, Iperf3Role.RECEIVER, port);

        // then
        assertTrue(TCPReceiverInstanceRunning);
    }
}
