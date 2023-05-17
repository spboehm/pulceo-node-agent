package dev.pulceo.pna;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
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
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testStartIperf3Server() throws BandwidthServiceException, InterruptedException {
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
    public void testStartTooManyIperf3Instances() throws BandwidthServiceException, InterruptedException {
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
    public void testStopIperf3ServerByPort() throws InterruptedException, BandwidthServiceException, IOException {
        // given
        int port = 5001;
        // start iperf3 receiver
        startIperf3ServerInstance(port);

        // when
        bandwidthService.stopIperf3Server(5001);
        boolean iperf3ServerInstanceRunning = bandwidthService.checkForRunningIperf3Receiver(port);

        // then
        assertFalse(iperf3ServerInstanceRunning);
    }

    @Test
    public void testStopIperf3ServerByPid() throws BandwidthServiceException {
        // given
        long pid = bandwidthService.startIperf3Server();

        // when
        bandwidthService.stopIperf3Server(pid);
        boolean iperf3ServerInstanceRunning = bandwidthService.checkForRunningIperf3Receiver(5001);

        // then
        assertFalse(iperf3ServerInstanceRunning);
    }

    @Test
    public void testStartMultipleIperf3ServerInstancesAndThenStartAnIperf3ServerInstance() throws BandwidthServiceException, InterruptedException, IOException {
        // given
        int numberOfIperf3ServerInstances = 16;

        List<Long> pids = new ArrayList<>();
        for (int i = 0; i < numberOfIperf3ServerInstances; i++) {
            startIperf3ServerInstance(5000 + i);
        }

        bandwidthService.stopIperf3Server(5012);

        // when
        long pid = bandwidthService.startIperf3Server();

        // then
        assertTrue(pid > 0);
    }

    @Test
    public void testGetListOfRunningIperf3Instances() throws BandwidthServiceException, ProcessException, IOException, InterruptedException {
        // given
        int expectedNumberOfIperf3ServerInstances = 16;
        List<Long> pids = new ArrayList<>();
        for (int i = 0; i < expectedNumberOfIperf3ServerInstances; i++) {
            startIperf3ServerInstance(5000 + i);
        }

        // when
        List<String> actualListOfRunningIperf3Instances = bandwidthService.getListOfRunningIperf3Instances();

        // then
        Assertions.assertEquals(expectedNumberOfIperf3ServerInstances, actualListOfRunningIperf3Instances.size());
    }

    @Test
    public void testCheckForRunningIperf3UDPSenderInstance() throws InterruptedException, IOException, BandwidthServiceException {
        // given
        int port = 5001;
        startIperf3ServerInstance(port);

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
        startIperf3ServerInstance(port);

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

    public static Process startIperf3ServerInstance(int port) throws IOException, InterruptedException {
        Process iperf3ServerInstance = new ProcessBuilder("/bin/iperf3", "-s", "-p", String.valueOf(port) ,"-f", "m").start();
        while (!iperf3ServerInstance.isAlive() ) {
            Thread.sleep(1000);
        }
        return iperf3ServerInstance;
    }

    @Test
    public void testCheckForRunningIperf3ReceiverInstance() throws IOException, InterruptedException, BandwidthServiceException {
        // given
        int port = 5001;
        startIperf3ServerInstance(port);

        // when
        boolean iperf3ReceiverInstanceRunning = bandwidthService.checkForRunningIperf3Receiver(port);

        // then
        assertTrue(iperf3ReceiverInstanceRunning);
    }

    @Test
    public void testGetPidOfRunningIperf3Receiver() throws BandwidthServiceException, IOException, InterruptedException {
        // given
        int port = 5001;
        Process iperf3ServerInstance = startIperf3ServerInstance(port);
        long expectedPidOfRunningIperf3Receiver = iperf3ServerInstance.pid();

        // when
        long actualPidOfRunningIperf3Receiver = bandwidthService.getPidOfRunningIperf3Receiver(port);

        // then
        assertEquals(expectedPidOfRunningIperf3Receiver, actualPidOfRunningIperf3Receiver);
    }

    @Test
    public void testMeasureBandwidth() throws IOException, InterruptedException, BandwidthServiceException {
        // given
        int port = 5001;
        startIperf3ServerInstance(port);

        // when
        //Iperf3Result iperf3Result = bandwidthService.measureBandwidth("localhost", port, Iperf3ClientProtocol.TCP);

        // then



    }
}