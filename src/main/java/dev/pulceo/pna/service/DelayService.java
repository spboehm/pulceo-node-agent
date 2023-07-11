package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.nping.*;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class DelayService {

    @Value("${pna.hostname:localhost}")
    private String sourceHost;

    @Value("${pna.delay.udp.port:4001}")
    private int npingDelayUDPPort;

    @Value("${pna.delay.tcp.port:4002}")
    private int npingDelayTCPPort;

    @Value("${pna.nping.rounds:10}")
    private int rounds;

    @Value("${pna.nping.interface:eth0}")
    private String iface;

    @Autowired
    PublishSubscribeChannel delayServiceMessageChannel;

    public boolean checkForRunningNpingInstance(NpingClientProtocol npingClientProtocol, String host, int port) throws DelayServiceException {
        try {
            List<String> listOfRunningNpingInstances = ProcessUtils.getListOfRunningProcessesByName("nping");
            for (String runningNpingInstance : listOfRunningNpingInstances) {
                if (npingClientProtocol == NpingClientProtocol.TCP) {
                    if (NpingUtils.isTCP(runningNpingInstance)) {
                        return checkForRunningNpingInstanceByHostAndPort(host, port, runningNpingInstance);
                    }
                } else {
                    if (NpingUtils.isUDP(runningNpingInstance)) {
                        return checkForRunningNpingInstanceByHostAndPort(host, port, runningNpingInstance);
                    }
                }
            }
            return false;
        } catch (ProcessException e) {
            throw new DelayServiceException("Could not check for running nping instance!", e);
        }
    }

    private boolean checkForRunningNpingInstanceByHostAndPort(String host, int port, String runningNpingInstance) {
        // extract host from nping cmd
        if (NpingUtils.extractHostFromNpingCmd(runningNpingInstance).equals(host)) {
            return NpingUtils.extractPortFromNpingCmd(runningNpingInstance) == port;
        }
        return false;
    }

    public NpingUDPResult measureUDPDelay(String destinationHost) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process npingProcess = new ProcessBuilder(new NpingClientCmd(NpingClientProtocol.UDP, this.npingDelayUDPPort, this.rounds, destinationHost, this.iface).getNpingCommandAsList()).start();
            if (npingProcess.waitFor() == 1) {
                List<String> strings = ProcessUtils.readProcessOutput(npingProcess.getErrorStream());
                throw new ProcessException(List.of(strings).toString());
            }
            String end = Instant.now().toString();
            List<String> npingProcessOutput = ProcessUtils.readProcessOutput(npingProcess.getInputStream());
            NpingUDPDelayMeasurement npingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, npingProcessOutput);
            return new NpingUDPResult(this.sourceHost, destinationHost, start, end, npingUDPDelayMeasurement);
        } catch (IOException | InterruptedException | ProcessException | NpingException e) {
            throw new DelayServiceException("Could not measure UDP delay!", e);
        }

    }

    public NpingTCPResult measureTCPDelay(String destinationHost) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process npingProcess = new ProcessBuilder(new NpingClientCmd(NpingClientProtocol.TCP, this.npingDelayTCPPort, this.rounds, destinationHost, this.iface).getNpingCommandAsList()).start();
            if (npingProcess.waitFor() == 1) {
                List<String> strings = ProcessUtils.readProcessOutput(npingProcess.getErrorStream());
                throw new ProcessException(List.of(strings).toString());
            }
            String end = Instant.now().toString();
            List<String> npingProcessOutput = ProcessUtils.readProcessOutput(npingProcess.getInputStream());
            NpingTCPDelayMeasurement npingTCPDelayMeasurement = NpingUtils.extractNpingTCPDelayMeasurement(NpingClientProtocol.TCP, npingProcessOutput);
            return new NpingTCPResult(this.sourceHost, destinationHost, start, end, npingTCPDelayMeasurement);
        } catch (IOException | InterruptedException | ProcessException | NpingException e) {
            throw new DelayServiceException("Could not measure TCP delay!", e);
        }
    }
    
}
