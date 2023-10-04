package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.PingException;
import dev.pulceo.pna.exception.PingServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.util.PingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class PingService {

    public PingResult measureRoundTripTime(PingRequest pingRequest) throws PingServiceException {
        try {
            String start = Instant.now().toString();
            Process pingProcess = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(pingRequest.getCmd())).start();
            if (pingProcess.waitFor() == 1) {
                List<String> strings = ProcessUtils.readProcessOutput(pingProcess.getErrorStream());
                throw new ProcessException(List.of(strings).toString());
            }
            String end = Instant.now().toString();
            List<String> pingProcessOuput = ProcessUtils.readProcessOutput(pingProcess.getInputStream());
            PingDelayMeasurement pingDelayMeasurement = PingUtils.extractPingDelayMeasurement(pingProcessOuput);
            return new PingResult(pingRequest.getSourceHost(), pingRequest.getDestinationHost(), start, end, pingDelayMeasurement);
        } catch (ProcessException | IOException | InterruptedException | PingException e) {
            throw new PingServiceException("Could not measure ICMP delay!", e);
        }
    }

}
