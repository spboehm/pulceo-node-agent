package dev.pulceo.pna;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
import dev.pulceo.pna.model.iperf3.Iperf3Result;
import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
    }

    @Test
    void testCheckForRunningIperf3ServerInstances() throws IOException, InterruptedException, ProcessException {
        // given
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                int port = 5001;
                Process p = new ProcessBuilder("iperf3", "-s", "-p", String.valueOf(5001)).start();
                p.waitFor();
            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        executorService.shutdown();

        // when
        boolean result = bandwidthService.checkForRunning(5001);

        // then
        assertEquals(true, result);
    }

    @Test
    @Disabled
    void testMeasureBandwidth() throws BandwidthServiceException {
        // given

        // when
        Iperf3Result iperf3Result = bandwidthService.measureBandwidth("localhost", 5001, Iperf3Protocol.TCP);

        // then
        System.out.println(iperf3Result.toString());
    }

}
