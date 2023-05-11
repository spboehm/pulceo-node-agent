package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.util.ProcessOutputReader;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

@Service
public class BandwidthService {

    public String createIperf3TCPServer(int port) {

        try {
            //
            Process p1 = new ProcessBuilder("/bin/iperf3", "-s", "-p 5001").start();
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
            ProcessOutputReader.readProcessOutput(p);
        }

        return "";
    }



}
