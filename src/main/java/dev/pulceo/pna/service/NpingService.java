package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.nping.*;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class NpingService {

    @Value("${pna.host:localhost}")
    private String sourceHost;

    @Value("${pna.delay.udp.port:4001}")
    private int npingDelayUDPPort;

    @Value("${pna.delay.tcp.port:4002}")
    private int npingDelayTCPPort;

    @Value("${pna.delay.rounds:10}")
    private int rounds;

    @Value("${pna.delay.interface:eth0}")
    private String iface;

    @Value("${pna.delay.udp.data.length}")
    private int dataLength;

    public boolean checkForRunningNpingInstance(NpingClientProtocol npingClientProtocol, String host) throws DelayServiceException {
        try {
            List<String> listOfRunningNpingInstances = ProcessUtils.getListOfRunningProcessesByName("nping");
            for (String runningNpingInstance : listOfRunningNpingInstances) {
                if (npingClientProtocol == NpingClientProtocol.TCP) {
                    if (NpingUtils.isTCP(runningNpingInstance)) {
                        return isHostPartOfRunningNpingInstance(host, runningNpingInstance);
                    }
                } else {
                    if (NpingUtils.isUDP(runningNpingInstance)) {
                        return isHostPartOfRunningNpingInstance(host, runningNpingInstance);
                    }
                }
            }
            return false;
        } catch (ProcessException e) {
            throw new DelayServiceException("Could not check for running nping instance!", e);
        }
    }

    private boolean isHostPartOfRunningNpingInstance(String host, String runningNpingInstance) {
        return NpingUtils.extractHostFromNpingCmd(runningNpingInstance).equals(host);
    }

    public NpingUDPResult measureUDPDelay(String destinationHost, String iface) throws DelayServiceException {
        return this.measureUDPDelay(destinationHost, this.dataLength, iface);
    }


    public NpingUDPResult measureUDPDelay(String destinationHost, int dataLength, String iface) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process npingProcess = getNpingProcess(NpingClientProtocol.UDP, this.npingDelayUDPPort, destinationHost, iface);
            String end = Instant.now().toString();
            List<String> npingProcessOutput = ProcessUtils.readProcessOutput(npingProcess.getInputStream());
            NpingUDPDelayMeasurement npingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, npingProcessOutput);
            return new NpingUDPResult(this.sourceHost, destinationHost, start, end, dataLength ,npingUDPDelayMeasurement);
        } catch (IOException | InterruptedException | ProcessException | NpingException e) {
            throw new DelayServiceException("Could not measure UDP delay!", e);
        }

    }

    private Process getNpingProcess(NpingClientProtocol npingClientProtocol, int npingPort, String destinationHost, String iface) throws IOException, InterruptedException, ProcessException {
        Process npingProcess = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(new NpingRequest(this.sourceHost, destinationHost, npingPort, npingClientProtocol, this.rounds, iface).getCmd())).start();
        if (npingProcess.waitFor() == 1) {
            List<String> strings = ProcessUtils.readProcessOutput(npingProcess.getErrorStream());
            throw new ProcessException(List.of(strings).toString());
        }
        return npingProcess;
    }

    public NpingTCPResult measureTCPDelay(String destinationHost, String iface) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process npingProcess = getNpingProcess(NpingClientProtocol.TCP, this.npingDelayTCPPort, destinationHost, iface);
            String end = Instant.now().toString();
            List<String> npingProcessOutput = ProcessUtils.readProcessOutput(npingProcess.getInputStream());
            NpingTCPDelayMeasurement npingTCPDelayMeasurement = NpingUtils.extractNpingTCPDelayMeasurement(NpingClientProtocol.TCP, npingProcessOutput);
            return new NpingTCPResult(this.sourceHost, destinationHost, start, end, npingTCPDelayMeasurement);
        } catch (IOException | InterruptedException | ProcessException | NpingException e) {
            throw new DelayServiceException("Could not measure TCP delay!", e);
        }
    }
    
}
