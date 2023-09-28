package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPResult;
import dev.pulceo.pna.model.ping.PingRequest;
import dev.pulceo.pna.model.ping.PingResult;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

@Service
public class PingService {

    public PingResult measureRoundTripTime(PingRequest pingRequest) throws DelayServiceException {
        try {
            String start = Instant.now().toString();
            Process pingProcess = new ProcessBuilder(ProcessUtils.splitCmdByWhitespaces(pingRequest.getCmd())).start();
            String end = Instant.now().toString();
            List<String> pingProcessOuput = ProcessUtils.readProcessOutput(pingProcess.getInputStream());
            //NpingUDPDelayMeasurement npingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, npingProcessOutput);
            //return new NpingUDPResult(this.sourceHost, destinationHost, start, end, dataLength ,npingUDPDelayMeasurement);
        } catch (IOException  | ProcessException e) {
            throw new DelayServiceException("Could not measure UDP delay!", e);
        }
        //return new PingResult();
    }

}
