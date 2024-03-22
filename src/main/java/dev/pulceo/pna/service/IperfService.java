package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.BandwidthServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf.*;
import dev.pulceo.pna.repository.IperfServerRequestRepository;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class IperfService {

    private final Logger logger = LoggerFactory.getLogger(IperfService.class);

    @Value("${pna.host}")
    private String hostname;

    @Value("${pna.iperf3.max.server.instances:16}")
    private Integer maxNumberOfIperf3Instances;

    @Value("${pna.iperf3.bind}")
    private String bind;

    @Autowired
    PublishSubscribeChannel bandwidthServiceMessageChannel;

    private final IperfServerRequestRepository iperfServerRequestRepository;

    @Autowired
    public IperfService(IperfServerRequestRepository iperfServerRequestRepository) {
        this.iperfServerRequestRepository = iperfServerRequestRepository;
    }

    public long startIperf3ServerAndReturnPort() throws BandwidthServiceException {
        try {
            int nextAvailablePort = getNextAvailablePort();
            IperfServerCmd iperfServerCmd = new IperfServerCmd(nextAvailablePort, bind);
            Process iperf3Process = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(iperfServerCmd.getCmd())).start();
            ProcessUtils.waitUntilProcessIsAlive(iperf3Process);
            this.iperfServerRequestRepository.save(IperfServerRequest.builder().port(nextAvailablePort).build());
            return nextAvailablePort;
        } catch (IOException | InterruptedException | ProcessException e) {
            logger.error("Could not start Iperf3 server process!", e);
            throw new BandwidthServiceException("Could not start Iperf3 server process!", e);
        }
    }

    public long startIperf3Server() throws BandwidthServiceException {
        try {
            int nextAvailablePort = getNextAvailablePort();
            IperfServerCmd iperfServerCmd = new IperfServerCmd(nextAvailablePort, bind);
            ProcessBuilder iperf3ProcessBuilder = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(iperfServerCmd.getCmd()));
            iperf3ProcessBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
            iperf3ProcessBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
            Process iperf3Process = iperf3ProcessBuilder.start();
            return ProcessUtils.waitUntilProcessIsAlive(iperf3Process);
            // TODO: persist iperf3 server process
        } catch (IOException | InterruptedException | ProcessException e) {
            logger.error("Could not start Iperf3 server process!", e);
            throw new BandwidthServiceException("Could not start Iperf3 server process!", e);
        }
    }

    @PostConstruct
    private void init()  {
        try {
            // kill all iperf processes
            Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
            p.waitFor();
            // TODO: eventuall kill all iperf3 processes
            Iterable<IperfServerRequest> iperfServerRequests = this.iperfServerRequestRepository.findAll();
            for (IperfServerRequest iperfServerRequest : iperfServerRequests) {
                try {
                    // TODO: refactor and streamline with startIperf3Server
                    Process iperf3Process = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(new IperfServerCmd(iperfServerRequest.getPort(), bind).getCmd())).start();
                    ProcessUtils.waitUntilProcessIsAlive(iperf3Process);
                } catch (IOException | InterruptedException e) {
                    logger.error("Could not start Iperf3 server processes!", e);
                    throw new RuntimeException("Could not start Iperf3 server processes!", e);
                }
            }
        } catch (IOException | InterruptedException e) {
            logger.error("Could not start Iperf3 server processes!", e);
            throw new RuntimeException("Could not start Iperf3 server processes!", e);
        }

    }

    // TODO: synchronization
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

    // TODO: rename by port
    public void stopIperf3Server(int port) throws BandwidthServiceException {
        Process killIperf3Server = null;
        try {
            if (!checkForRunningIperf3Receiver(port)) {
                return;
            }

            killIperf3Server = new ProcessBuilder("kill", "-9", String.valueOf(getPidOfRunningIperf3Receiver(port))).start();
            killIperf3Server.waitFor();

            // wait for termination
            while(checkForRunningIperf3Receiver(port)) {
                Thread.sleep(100);
            }
        } catch (InterruptedException | IOException e) {
            throw new BandwidthServiceException("Could not stop iperf3 server!", e);
        } finally {
            try {
                ProcessUtils.closeProcess(killIperf3Server);
            } catch (IOException e) {
                logger.error("Could not close iperf3 process!", e);
            }
        }
    }

    // TODO: rename by pid
    public void stopIperf3Server(long pid) throws BandwidthServiceException {
        Process p = null;
        try {
            p = new ProcessBuilder("kill", String.valueOf(pid)).start();
            p.waitFor();
        } catch (InterruptedException | IOException e) {
            throw new BandwidthServiceException("Could not stop iperf3 server!", e);
        } finally {
            try {
                ProcessUtils.closeProcess(p);
            } catch (IOException e) {
                logger.error("Could not close iperf3 process!", e);
            }
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

    public IperfResult measureBandwidth(IperfRequest iperfRequest) throws BandwidthServiceException {
        Process p = null;
        try {
            logger.debug("Measuring bandwidth from {} to {}!", iperfRequest.getSourceHost(), iperfRequest.getDestinationHost());
            String start = Instant.now().toString();
            p = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(iperfRequest.getCmd())).start();
            // TODO: handle error caused by iperf, if remote server could not be found; error = 1; success = 0;
            p.waitFor((long) iperfRequest.getTime() * 3, TimeUnit.SECONDS);
            if (p.exitValue() != 0) {
                List<String> strings = ProcessUtils.readProcessOutput(p.getErrorStream());
                logger.error("Could not measure bandwidth from {} to {}: {}", iperfRequest.getSourceHost(), iperfRequest.getDestinationHost(), List.of(strings));
                throw new ProcessException("Could not measure bandwidth!");
            }
            String end = Instant.now().toString();
            List<String> iperf3Output = ProcessUtils.readProcessOutput(p.getInputStream());
            IperfBandwidthMeasurement iperfBandwidthMeasurementSender = Iperf3Utils.extractIperf3BandwidthMeasurement(iperfRequest.getIperfClientProtocol(), iperf3Output, IperfRole.SENDER);
            IperfBandwidthMeasurement iperfBandwidthMeasurementReceiver = Iperf3Utils.extractIperf3BandwidthMeasurement(iperfRequest.getIperfClientProtocol(), iperf3Output, IperfRole.RECEIVER);
            return new IperfResult(iperfRequest.getSourceHost(), iperfRequest.getDestinationHost(), start, end, iperfBandwidthMeasurementReceiver, iperfBandwidthMeasurementSender);
        } catch (InterruptedException | IOException | ProcessException e) {
            logger.error("Could not measure bandwidth from {} to {}: {}", iperfRequest.getSourceHost(), iperfRequest.getDestinationHost(), e.getMessage());
            throw new BandwidthServiceException("Could not measure bandwidth!", e);
        } finally {
            try {
                ProcessUtils.closeProcess(p);
            } catch (IOException e) {
                logger.error("Could not close iperf3 process!", e);
            }
        }
    }
}
