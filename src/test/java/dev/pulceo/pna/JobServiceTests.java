package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.job.IperfJob;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;

import java.io.IOException;

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
        Assertions.assertTrue(id > 0);
    }

    @Test
    public void testCreatedIperfJobIsInactive() throws JobServiceException {
        // given
        IperfJob iperfJob = new IperfJob("localhost", "localhost", 5001, IperfClientProtocol.TCP, 15);

        // when
        long savedIperfJob = this.jobService.createIperfJob(iperfJob);
        IperfJob retrievedIperfJob = this.jobService.readIperfJob(savedIperfJob);

        // then
        Assertions.assertFalse(retrievedIperfJob.isActive());
    }

    @Test
    public void testScheduleBandwidthJob() throws Exception {
        // given
        int port = 5001;
        BandwidthServiceTests.startIperf3ServerInstance(port);
        int recurrence = 15;
        IperfJob iperfJob = new IperfJob("localhost", "localhost", port, IperfClientProtocol.TCP, 15);
        long id = this.jobService.createIperfJob(iperfJob);

        // when
        long localJobId = this.jobService.scheduleIperfJob(id);
        //Thread.sleep(recurrence * 1000);

        // then
        Message<?> message = this.jobServiceChannel.receive();
        this.jobService.cancelIperfJob((int) localJobId);

        // todo wait for result

    }

}
