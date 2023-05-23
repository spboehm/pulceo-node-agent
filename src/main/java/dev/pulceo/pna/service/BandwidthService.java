package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.*;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class BandwidthService {

    @Value("${pna.hostname}")
    private String hostname;

    @Value("${pna.iperf3.max.server.instances:16}")
    private Integer maxNumberOfIperf3Instances;

    @Autowired
    private PollableChannel jobServiceChannel;

    public long startIperf3Server() throws BandwidthServiceException {
        try {
            int nextAvailablePort = getNextAvailablePort();
            IperfServerCmd iperfServerCmd = new IperfServerCmd(nextAvailablePort);
            Process iperf3Process = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(iperfServerCmd.getCmd())).start();
            return ProcessUtils.waitUntilProcessIsAlive(iperf3Process);
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new BandwidthServiceException("Could not start Iperf3 server process!", e);
        }
    }

    private int getNextAvailablePort() throws BandwidthServiceException, ProcessException {
        List<String> listOfRunningIperf3ServerInstances = this.getListOfRunningIperf3Instances();
        List<Integer> availablePorts = new ArrayList<Integer>();
        for (int i = 0; i < this.maxNumberOfIperf3Instances; i++) {
            availablePorts.add(5000 + i);
        }
        for (String runningIperf3ServerInstance : listOfRunningIperf3ServerInstances) {
            availablePorts.remove(Integer.valueOf(Iperf3Utils.extractPortFromIperf3Cmd(runningIperf3ServerInstance)));
        }
        if (availablePorts.size() > 0) {
            return availablePorts.get(0);
        } else {
            throw new BandwidthServiceException("No ports available!");
        }
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

    public void stopIperf3Server(long pid) throws BandwidthServiceException {
        try {
            Process p = new ProcessBuilder("kill", String.valueOf(pid)).start();
            p.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new BandwidthServiceException("Could not stop iperf3 server!", e);
        }
    }

    public List<String> getListOfRunningIperf3Instances() throws ProcessException {
        List<String> listOfRunningIperf3Processes = ProcessUtils.getListOfRunningProcessesByName("iperf3");
        List<String> listOfRunningIperf3ServerInstances = new ArrayList<>();
        for (String listOfRunningIperf3Process : listOfRunningIperf3Processes) {
            String cmdOfpsEntry = ProcessUtils.getCmdOfpsEntry(listOfRunningIperf3Process);
            if (Iperf3Utils.isReceiver(cmdOfpsEntry)) {
                listOfRunningIperf3ServerInstances.add(cmdOfpsEntry);
            }
        }
        return listOfRunningIperf3ServerInstances;
    }

    public boolean checkForRunningIperf3Sender(IperfClientProtocol iperf3Protocol, String host, int port) throws BandwidthServiceException {
        try {
            List<String> listOfRunningIperf3Instances = ProcessUtils.getListOfRunningProcessesByName("iperf3");
            for (String runningIperf3Instance : listOfRunningIperf3Instances) {
                if (iperf3Protocol == IperfClientProtocol.TCP) {
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
            throw new BandwidthServiceException("Could not determine if iperf3 instance is running!", e);
        }
    }

    public boolean checkForRunningIperf3Receiver(int port) throws BandwidthServiceException {
        return (getPidOfRunningIperf3Receiver(port) != -1);
    }

    @Async
    public CompletableFuture<IperfResult> measureBandwidth(IperfJob iperfJob) {
        try {
            String start = Instant.now().toString();
            Process p;
            if (iperfJob.getIperfClientProtocol() == IperfClientProtocol.TCP) {
                p = new ProcessBuilder("/bin/iperf3", "-c", iperfJob.getDestinationHost(), "-p", String.valueOf(iperfJob.getPort()), "-f", "m").start();
            } else {
                p = new ProcessBuilder("/bin/iperf3", "-u", "-c", iperfJob.getDestinationHost(), "-p", String.valueOf(iperfJob.getPort()), "-f m").start();
            }
            p.waitFor();
            String end = Instant.now().toString();
            List<String> iperf3Output = ProcessUtils.readProcessOutput(p.getInputStream());
            IperfBandwidthMeasurement iperfBandwidthMeasurementSender = Iperf3Utils.extractIperf3BandwidthMeasurement(iperfJob.getIperfClientProtocol(), iperf3Output, IperfRole.SENDER);
            IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver = Iperf3Utils.extractIperf3BandwidthMeasurement(iperfJob.getIperfClientProtocol(), iperf3Output, IperfRole.RECEIVER);
            IperfResult iperfResult = new IperfResult(iperfJob.getSourceHost(), iperfJob.getDestinationHost(), start, end, iperfBandwidthMeasurementSender, iperfBandwidthMeasurementReceiver);
            this.jobServiceChannel.send(new GenericMessage<>(iperfResult));
            return CompletableFuture.completedFuture(iperfResult);
        } catch (InterruptedException | IOException | ProcessException e) {
            throw new RuntimeException("Could not measure bandwidth!", e);
        }
    }

}
