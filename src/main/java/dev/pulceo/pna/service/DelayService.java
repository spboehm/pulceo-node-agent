package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.DelayServiceException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.job.NpingJob;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DelayService {

    @Value("${pna.hostname:localhost}")
    private String hostname;

    @Value("${pna.nping.interface:eth0}")
    private String iface;

    // SUT
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


        // extract port from nping cmd

        return false;
    }

    public void measureDelay(NpingJob npingJob) {

    }

}
