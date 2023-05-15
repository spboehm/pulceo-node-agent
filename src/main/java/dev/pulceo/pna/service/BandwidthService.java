package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
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
    
    public String startIperf3TCPServer(int port, Iperf3Protocol iperf3Protocol) {

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

    public boolean checkForRunningIperf3Instance(Iperf3Protocol iperf3Protocol, Iperf3Role iperf3Role, int port) throws IOException, InterruptedException, ProcessException {

        // get list of running processes

        // TCP
        // UDP -u

        // Role sender / receiver

        // port 5001...

        return false;
    }

    // sender-side
    public Iperf3Result measureBandwidth(String host, int port, Iperf3Protocol protocol) throws BandwidthServiceException {
        try {
            String from  = environment.getProperty("pna.hostname");

            String start = Instant.now().toString();
            Process p;
            if (protocol == Iperf3Protocol.TCP) {
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
