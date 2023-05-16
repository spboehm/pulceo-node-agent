package dev.pulceo.pna;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class BandwidthServiceTests {

    @Autowired
    BandwidthService bandwidthService;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
    }

    @Test
    public void testCheckForRunningIperf3UDPSenderInstance() throws InterruptedException, IOException, BandwidthServiceException {
        // given
        int port = 5001;
        // start iperf3 receiver
        Process iperf3Receiver = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port), "-f", "m").start();
        while (!iperf3Receiver.isAlive() ) {
            Thread.sleep(1000);
        }

        // start iperf3 sender
        String host = "localhost";
        Process iperf3Sender = new ProcessBuilder("/bin/iperf3", "-c", host, "-u", "-p", String.valueOf(port), "-f", "m", "-t", "60").start();
        while (!iperf3Sender.isAlive()) {
            Thread.sleep(1000);
        }

        // when
        boolean iperf3UDPSenderInstanceRunning = bandwidthService.checkForRunningIperf3Sender(Iperf3ClientProtocol.UDP, host, port);

        // then
        assertTrue(iperf3UDPSenderInstanceRunning);
    }

    @Test
    public void testCheckForRunningIperf3TCPSenderInstance() throws InterruptedException, IOException, BandwidthServiceException {
        // given
        int port = 5001;
        // start iperf3 receiver
        Process iperf3Receiver = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port) ,"-f", "m").start();
        while (!iperf3Receiver.isAlive() ) {
            Thread.sleep(1000);
        }

        // start iperf3 sender
        String host = "localhost";
        Process iperf3Sender = new ProcessBuilder("/bin/iperf3", "-c", host, "-p", String.valueOf(port), "-f", "m", "-t", "60").start();
        while (!iperf3Sender.isAlive()) {
            Thread.sleep(1000);
        }

        // when
        boolean iperf3TCPSenderInstanceRunning = bandwidthService.checkForRunningIperf3Sender(Iperf3ClientProtocol.TCP, host, port);

        // then
        assertTrue(iperf3TCPSenderInstanceRunning);
    }

    @Test
    public void testCheckForRunningIperf3ReceiverInstance() throws IOException, InterruptedException, BandwidthServiceException {
        // given
        int port = 5001;
        Process p = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port), "-f", "m").start();

        while (!p.isAlive()) {
            Thread.sleep(1000);
        }

        // when
        boolean iperf3ReceiverInstanceRunning = bandwidthService.checkForRunningIperf3Receiver(port);

        // then
        assertTrue(iperf3ReceiverInstanceRunning);
    }
}
