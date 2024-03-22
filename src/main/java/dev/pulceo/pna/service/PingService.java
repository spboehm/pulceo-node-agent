package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.PingException;
import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.util.PingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PingService {

    private final Logger logger = LoggerFactory.getLogger(PingService.class);

    public boolean checkForRunningPingInstance(String destinationHost) throws PingServiceException {
        try {
            List<String> listOfRunningPingInstances = ProcessUtils.getListOfRunningProcessesByName("/usr/bin/ping");
            for (String runningPingInstance : listOfRunningPingInstances) {
                return isHostPartofRunningPingInstanceCmd(destinationHost, runningPingInstance);
            }
        } catch (ProcessException e) {
            throw new PingServiceException("Could not check for running ping instance!", e);
        }
        return false;
    }

    private boolean isHostPartofRunningPingInstanceCmd(String host, String runningPingInstance) {
        return PingUtils.extractHostFromPingCmd(runningPingInstance).equals(host);
    }

    public PingResult measureRoundTripTime(PingRequest pingRequest) throws PingServiceException {
        Process pingProcess = null;
        try {
            logger.debug("Measuring ICMP delay from {} to {}!", pingRequest.getSourceHost(), pingRequest.getDestinationHost());
            String start = Instant.now().toString();
            pingProcess = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(pingRequest.getCmd())).start();
            pingProcess.waitFor((long) pingRequest.getCount() * 3, TimeUnit.SECONDS);
            if (pingProcess.exitValue() != 0) {
                List<String> strings = ProcessUtils.readProcessOutput(pingProcess.getErrorStream());
                logger.error("Could not measure ICMP delay: {}", strings);
                throw new ProcessException(List.of(strings).toString());
            }
            String end = Instant.now().toString();
            List<String> pingProcessOuput = ProcessUtils.readProcessOutput(pingProcess.getInputStream());
            PingDelayMeasurement pingDelayMeasurement = PingUtils.extractPingDelayMeasurement(pingProcessOuput);
            return new PingResult(pingRequest.getSourceHost(), pingRequest.getDestinationHost(), start, end, pingDelayMeasurement);
        } catch (ProcessException | IOException | InterruptedException | PingException e) {
            logger.error("Could not measure ICMP delay!", e);
            throw new PingServiceException("Could not measure ICMP delay!", e);
        } finally {
            try {
                ProcessUtils.closeProcess(pingProcess);
            } catch (IOException e) {
                logger.error("Could not close process", e);
            }
        }
    }

}
