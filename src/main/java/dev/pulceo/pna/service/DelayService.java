package dev.pulceo.pna.service;

import dev.pulceo.pna.model.job.NpingJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import org.springframework.stereotype.Service;

@Service
public class DelayService {

    public boolean checkForRunningNpingInstance(NpingClientProtocol npingClientProtocol, String host, int port) {
        // either tcp or udp

        return false;
    }

    private boolean checkForRunningNpingInstanceByHostAndPort(String host, int port, String runningNpingInstance) {
        // either t
        return false;
    }

    public void measureDelay(NpingJob npingJob) {

    }


}
