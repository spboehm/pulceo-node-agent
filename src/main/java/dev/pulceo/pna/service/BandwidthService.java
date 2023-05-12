package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
import dev.pulceo.pna.model.iperf3.Iperf3Result;
import dev.pulceo.pna.util.ProcessOutputUtils;
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

    public String createIperf3TCPServer(int port) {

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


    public String checkForRunning(int port) throws IOException, InterruptedException, ProcessException {

        Process p = new ProcessBuilder("ps -ef | grep iperf3 | awk {'print $11'} | grep" + " " + port).start();
        int waitForCompleted = p.waitFor();

        if (waitForCompleted == 1)  {
            ProcessOutputUtils.readProcessOutput(p.getInputStream());
        }

        return "";
    }

    public Iperf3Result measureBandwidth(String host, int port, Iperf3Protocol protocol) {

        try {
            /* Iperf3Result */
            // from
            final String from  = environment.getProperty("pna.hostname");
            // to
            final String to = host;
            // start
            final String start = Instant.now().toString();
            Process p = new ProcessBuilder("/bin/iperf3", "-c", host ,"-p", String.valueOf(port),"-f m").start();
            p.waitFor();
            // end
            final String end = Instant.now().toString();
            // iperf3Output
            final List<String> iperf3Output = ProcessOutputUtils.readProcessOutput(p.getInputStream());
            // client
            // TODO: process
            // receiver
            // TODO: process

        } catch (IOException | InterruptedException | ProcessException e) {
            throw new RuntimeException(e);
        }

        return null;
    }


}
