package dev.pulceo.pna;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BandwidthServiceTests {

    @Autowired
    BandwidthService bandwidthService;

    @Autowired
    Environment environment;

    @Test
    void contextLoads() {
    }

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testStartIperf3Server() throws BandwidthServiceException {
        // given

        // when
        long pid = bandwidthService.startIperf3Server();

        // then
        assertTrue(pid > 0);
    }

    @Test
    public void testStartMultipleIperf3Server() throws BandwidthServiceException {
        // given
        int numberOfIperf3ServerInstances = 16;

        // when
        List<Long> pids = new ArrayList<>();
        for (int i = 0; i < numberOfIperf3ServerInstances; i++) {
            pids.add(bandwidthService.startIperf3Server());
        }

        // then
        for (int i = 0; i < numberOfIperf3ServerInstances; i++) {
            assertTrue(pids.get(i) > 0);
        }
    }

    @Test
    public void testStartTooManyIperf3Instances() {
        // given
        int numberOfIperf3ServerInstances = 32;

        // when
        BandwidthServiceException bandwidthServiceException = assertThrows(BandwidthServiceException.class, () -> {
            for (int i = 0; i < numberOfIperf3ServerInstances; i++) {
                bandwidthService.startIperf3Server();
            }
        });

        // then
        assertEquals("No ports available!", bandwidthServiceException.getMessage());
    }

    @Test
    public void testStopIperf3Server() throws InterruptedException, BandwidthServiceException, IOException {
        // given
        int port = 5001;
        // start iperf3 receiver
        Process iperf3Receiver = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port), "-f", "m").start();
        while (!iperf3Receiver.isAlive() ) {
            Thread.sleep(1000);
        }

        // when
        bandwidthService.stopIperf3Server(5001);
        boolean iperf3ServerInstanceRunning = bandwidthService.checkForRunningIperf3Receiver(port);

        // then
        assertFalse(iperf3ServerInstanceRunning);
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

    @Test
    public void testGetPidOfRunningIperf3Receiver() throws BandwidthServiceException, IOException, InterruptedException {
        // given
        int port = 5001;
        Process p = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port), "-f", "m").start();

        while (!p.isAlive()) {
            Thread.sleep(1000);
        }
        long expectedPidOfRunningIperf3Receiver = p.pid();

        // when
        long actualPidOfRunningIperf3Receiver = bandwidthService.getPidOfRunningIperf3Receiver(port);

        // then
        assertEquals(expectedPidOfRunningIperf3Receiver, actualPidOfRunningIperf3Receiver);
    }
}
