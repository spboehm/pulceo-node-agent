package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.model.iperf3.Iperf3Result;
import dev.pulceo.pna.model.iperf3.Iperf3Role;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class BandwidthService {

    private Environment environment;

    public BandwidthService(Environment environment) {
        this.environment = environment;
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
            if (Iperf3Utils.extractPortFromIperf3Cmd(runningIperf3Instance) == port) {
                return true;
            }
        }
        return false;
    }

    public boolean checkForRunningIperf3Receiver(int port) throws BandwidthServiceException {
        try {
            List<String> listOfRunningIperf3Instances = ProcessUtils.getListOfRunningProcessesByName("iperf3");
            for (String runningIperf3Instance : listOfRunningIperf3Instances) {
                if (Iperf3Utils.isReceiver(runningIperf3Instance) && Iperf3Utils.extractPortFromIperf3Cmd(runningIperf3Instance) == port) {
                    return true;
                }
            }
            return false;
        } catch (ProcessException e) {
            throw new BandwidthServiceException("Could determine if iperf3 instance is running!", e);
        }
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
