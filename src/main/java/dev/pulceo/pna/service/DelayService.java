package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.job.NpingJob;
import dev.pulceo.pna.model.nping.*;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class DelayService {

    @Value("${pna.hostname:localhost}")
    private String hostname;

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

    public void measureDelay(NpingJob npingJob) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process p;
            if (npingJob.getNpingClientProtocol() == NpingClientProtocol.TCP) {
                p = new ProcessBuilder("/usr/bin/nping", "-4", "--tcp-connect", "-c", String.valueOf(npingJob.getRecurrence()), "--dest-ip", npingJob.getDestinationHost(), "-p", String.valueOf(npingJob.getPort()), "-e", this.iface).start();
            } else {
                p = new ProcessBuilder("/usr/bin/nping", "-4", "--udp", "-c", String.valueOf(npingJob.getRecurrence()), "--dest-ip", npingJob.getDestinationHost(), "-p", String.valueOf(npingJob.getPort()), "-e", this.iface, "--data-length", "66").start();
            }
            // TODO: handle error caused by iperf, if remote server could not be found; error = 1; success = 0;
            if (p.waitFor() == 1) {
                List<String> strings = ProcessUtils.readProcessOutput(p.getErrorStream());
                throw new ProcessException(List.of(strings).toString());
            }

            String end = Instant.now().toString();
            List<String> npingOutput = ProcessUtils.readProcessOutput(p.getInputStream());
            if (npingJob.getNpingClientProtocol() == NpingClientProtocol.TCP) {
                NpingTCPDelayMeasurement npingTCPDelayMeasurement = NpingUtils.extractNpingTCPDelayMeasurement(NpingClientProtocol.TCP, npingOutput);
                NpingTCPResult npingTCPResult = new NpingTCPResult(hostname, npingJob.getDestinationHost(), start, end, npingTCPDelayMeasurement);
                this.delayServiceMessageChannel.send(new GenericMessage<>(npingTCPResult));
            } else {
                NpingUDPDelayMeasurement npingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, npingOutput);
                NpingUDPResult npingUDPResult = new NpingUDPResult(hostname, npingJob.getDestinationHost(), start, end, npingUDPDelayMeasurement);
                this.delayServiceMessageChannel.send(new GenericMessage<>(npingUDPResult));
            }

        } catch (IOException | InterruptedException | ProcessException | NpingException e) {
            throw new DelayServiceException("Could not measure delay!", e);
        }
    }




}
