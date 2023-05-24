package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class JobServiceTests {

    @Autowired
    JobService jobService;

    @Autowired
    PollableChannel jobServiceChannel;

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testCreateIperfTask() {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);

        // when
        long id  = this.jobService.createIperfJob(iperfJob);

        // then
        assertTrue(id > 0);
    }

    @Test
    public void testCreatedIperfJobIsInactive() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);

        // when
        long savedIperfJob = this.jobService.createIperfJob(iperfJob);
        IperfJob retrievedIperfJob = this.jobService.readIperfJob(savedIperfJob);

        // then
        assertFalse(retrievedIperfJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);
        // newly created job is disabled by default, means active = false
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob enabledIperfJob = this.jobService.enableIperfJob(savedIperfJobId);

        // then
        assertFalse(iperfJob.isEnabled());
        assertTrue(enabledIperfJob.isEnabled());
    }

    @Test
    public void testEnableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        iperfJob.setEnabled(true);
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob alreadyEnabledIperfJob = this.jobService.enableIperfJob(savedIperfJobId);

        // then
        assertTrue(iperfJob.isEnabled());
        assertTrue(alreadyEnabledIperfJob.isEnabled());

    }

    @Test
    public void testDisableIperfJobWithEnabledJob() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);
        // set to enabled, because newly created job is disabled by default, means active = false
        iperfJob.setEnabled(true);
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob enabledIperfJob = this.jobService.disableIperfJob(savedIperfJobId);

        // then
        assertTrue(iperfJob.isEnabled());
        assertFalse(enabledIperfJob.isEnabled());

    }

    @Test
    public void testDisableIperfJobWithDisabledJob() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);
        // newly created job is disabled by default, means active = false
        long savedIperfJobId = this.jobService.createIperfJob(iperfJob);

        // when
        IperfJob alreadyDisabledJob = this.jobService.disableIperfJob(savedIperfJobId);

        // then
        assertFalse(iperfJob.isEnabled());
        assertFalse(alreadyDisabledJob.isEnabled());

    }

    @Test
    public void testScheduleIperf3Job() throws Exception {
        // given
        int port = 5001;
        BandwidthServiceTests.startIperf3ServerInstance(port);
        int recurrence = 15;
        IperfJob iperfJob = new IperfJob("localhost", "localhost", port, IperfClientProtocol.TCP, recurrence);
        long id = this.jobService.createIperfJob(iperfJob);

        // when
        long localJobId = this.jobService.scheduleIperfJob(id);

        // then
        Message<?> message = this.jobServiceChannel.receive();
        this.jobService.cancelIperfJob((int) localJobId);

        // todo wait for result
        System.out.println(message.getPayload().toString());

    }

}
