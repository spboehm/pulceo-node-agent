package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.*;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class BandwidthService {

    private final Environment environment;

    private final AtomicInteger atomicInteger;

    public BandwidthService(Environment environment) {
        this.environment = environment;
        this.atomicInteger = new AtomicInteger(Integer.parseInt(environment.getProperty("pna.iperf3.max.server.instances")));
    }

    public long startIperf3Server() throws BandwidthServiceException {
        try {
            // get next available port
            int nextAvailablePort = this.getNextAvailablePort();
            Iperf3ServerCmd iperf3ServerCmd = new Iperf3ServerCmd(nextAvailablePort);
            Process iperf3Process = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(iperf3ServerCmd.getCmd())).start();
            return waitUntilProcessIsAlive(iperf3Process);
        } catch (IOException | InterruptedException e) {
            throw new BandwidthServiceException("Could not start Iperf3 server process!", e);
        }
    }

    private long waitUntilProcessIsAlive(Process iperf3Process) throws InterruptedException, IOException {
        if (iperf3Process.isAlive()) {
            return iperf3Process.pid();
        } else {
            Thread.sleep(5000);
            if (iperf3Process.isAlive()) {
                return iperf3Process.pid();
            } else {
                throw new IOException();
            }
        }
    }

    private int getNextAvailablePort() throws BandwidthServiceException {
        int nextPort = 5000 + this.atomicInteger.decrementAndGet();
        if (nextPort < 5000) {
            throw new BandwidthServiceException("No ports available!");
        }
        return nextPort;
    }

    public void stopIperf3Server(int port) throws BandwidthServiceException {
        try {
            if (!checkForRunningIperf3Receiver(port)) {
                return;
            }

            Process killIperf3Server = new ProcessBuilder("kill", "-9", String.valueOf(getPidOfRunningIperf3Receiver(port))).start();
            killIperf3Server.waitFor();

            // wait for termination
            while(checkForRunningIperf3Receiver(port)) {
                Thread.sleep(100);
            }
        } catch (InterruptedException | IOException e) {
            throw new BandwidthServiceException("Could not stop iperf3 server!", e);
        }
    }

    public boolean checkForRunningIperf3Sender(Iperf3ClientProtocol iperf3Protocol, String host, int port) throws BandwidthServiceException {
        try {
            List<String> listOfRunningIperf3Instances = ProcessUtils.getListOfRunningProcessesByName("iperf3");
            for (String runningIperf3Instance : listOfRunningIperf3Instances) {
                if (iperf3Protocol == Iperf3ClientProtocol.TCP) {
                    if (Iperf3Utils.isTCPSender(runningIperf3Instance)) {
                        return checkForRunningIperf3SenderByHostAndPort(host, port, runningIperf3Instance);
                    }
                } else {
                    if (Iperf3Utils.isUDPSender(runningIperf3Instance)) {
                        return checkForRunningIperf3SenderByHostAndPort(host, port, runningIperf3Instance);
                    }
                }
            }
            return false;
        } catch (ProcessException e) {
            throw new BandwidthServiceException("Could not check for running iperf3 sender process!", e);
        }
    }

    private boolean checkForRunningIperf3SenderByHostAndPort(String host, int port, String runningIperf3Instance) {
        if (Iperf3Utils.extractHostFromIperf3Cmd(runningIperf3Instance).equals(host)) {
            return Iperf3Utils.extractPortFromIperf3Cmd(runningIperf3Instance) == port;
        }
        return false;
    }

    public long getPidOfRunningIperf3Receiver(int port) throws BandwidthServiceException {
        try {
            List<String> listOfRunningIperf3Instances = ProcessUtils.getListOfRunningProcessesByName("iperf3");
            for (String runningIperf3Instance : listOfRunningIperf3Instances) {
                if (Iperf3Utils.isReceiver(runningIperf3Instance) && Iperf3Utils.extractPortFromIperf3Cmd(runningIperf3Instance) == port) {
                    return ProcessUtils.getPidOfpsEntry(runningIperf3Instance);
                }
            }
            return -1;
        } catch (ProcessException e) {
            throw new BandwidthServiceException("Could determine if iperf3 instance is running!", e);
        }
    }

    public boolean checkForRunningIperf3Receiver(int port) throws BandwidthServiceException {
        return (getPidOfRunningIperf3Receiver(port) != -1);
    }

    // sender-side
    public Iperf3Result measureBandwidth(String host, int port, Iperf3ClientProtocol protocol) throws BandwidthServiceException {
        try {
            String from  = environment.getProperty("pna.hostname");

            String start = Instant.now().toString();
            Process p;
            if (protocol == Iperf3ClientProtocol.TCP) {
                p = new ProcessBuilder("/bin/iperf3", "-c", "host", "-p", String.valueOf(port), "-f", "m").start();
            } else {
                p = new ProcessBuilder("/bin/iperf3", "-u" , "-c", host ,"-p", String.valueOf(port),"-f m").start();
            }
            p.waitFor();

            String end = Instant.now().toString();

            List<String> iperf3Output = ProcessUtils.readProcessOutput(p.getInputStream());

            Iperf3BandwidthMeasurement iperf3BandwidthMeasurementSender = Iperf3Utils.extractIperf3BandwidthMeasurement(protocol, iperf3Output, Iperf3Role.SENDER);
            Iperf3BandwidthMeasurement iperf3BandwidthMeasurementReceiver = Iperf3Utils.extractIperf3BandwidthMeasurement(protocol, iperf3Output, Iperf3Role.SENDER);

            return new Iperf3Result(
                    from, host,
                    start, end,
                    iperf3Output,
                    iperf3BandwidthMeasurementSender,
                    iperf3BandwidthMeasurementReceiver);
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new BandwidthServiceException("Could not measure bandwidth!", e);
        }
    }
}
