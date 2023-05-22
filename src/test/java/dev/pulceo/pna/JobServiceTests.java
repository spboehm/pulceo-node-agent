package dev.pulceo.pna;

import dev.pulceo.pna.model.BandwidthJob;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
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
        BandwidthJob bandwidthJob = new BandwidthJob("localhost", "localhost", 5001, Iperf3ClientProtocol.TCP, 5);

        // when
        long id  = this.jobService.createBandwidthJob(bandwidthJob);

        // then
        Assertions.assertTrue(id > 0);
    }

    @Test
    @Disabled
    public void testScheduleBandwidthJob() throws Exception {
        // given
        int port = 5001;
        BandwidthServiceTests.startIperf3ServerInstance(port);
        BandwidthJob bandwidthJob = new BandwidthJob("localhost", "localhost", port, Iperf3ClientProtocol.TCP, 30);
        long id = jobService.createBandwidthJob(bandwidthJob);

        // when
        jobService.scheduleIperf3BandwidthJob(id);
        Thread.sleep(90000);

        // then


    }

}
