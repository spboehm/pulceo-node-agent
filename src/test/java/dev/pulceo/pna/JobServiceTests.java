package dev.pulceo.pna;

import dev.pulceo.pna.model.tasks.IperfTask;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class JobServiceTests {

    @Autowired
    JobService jobService;

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testCreateBandwidthJob() {
        // given
        IperfTask iperfTask = new IperfTask("localhost", "localhost", 5001, IperfClientProtocol.TCP, 5);

        // when
        long id  = this.jobService.createBandwidthJob(iperfTask);

        // then
        Assertions.assertTrue(id > 0);
    }

    @Test
    @Disabled
    public void testScheduleBandwidthJob() throws Exception {
        // given
        int port = 5001;
        BandwidthServiceTests.startIperf3ServerInstance(port);
        IperfTask iperfTask = new IperfTask("localhost", "localhost", port, IperfClientProtocol.TCP, 15);
        long id = jobService.createBandwidthJob(iperfTask);

        // when
        jobService.scheduleIperf3BandwidthJob(id);
        Thread.sleep(20);

        // then



    }

}
