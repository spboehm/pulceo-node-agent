package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.model.iperf3.Iperf3Result;
import dev.pulceo.pna.model.iperf3.Iperf3Role;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class BandwidthService {

    @Autowired
    private Environment environment;
    
    public String startIperf3TCPServer(int port, Iperf3ClientProtocol iperf3Protocol) {

        try {
            //
            Process p1 = new ProcessBuilder("/bin/iperf3", "-s", "-p 5001", "-f m").start();
            //Process p2 = new ProcessBuilder("iperf3", "-s", "-p 5002").start();
            //Process p3 = new ProcessBuilder("iperf3", "-s", "-p 5003").start();


            return "";

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public String createIperf3UDPServer() {
        return "";
    }



    public boolean checkForRunningIperf3Sender(Iperf3ClientProtocol iperf3Protocol, String host, int port) {
        try {
            List<String> listOfRunningIperf3Instances = ProcessUtils.getListOfRunningProcessesByName("iperf3");
            for (String runningIperf3Instance: listOfRunningIperf3Instances) {

            }

            return false;
        } catch (ProcessException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean checkForRunningIperf3Receiver(int port) throws BandwidthServiceException {
        // Iperf3 Protocol is always TCP for receiver
        // Iperf3 Role is always Receiver
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
