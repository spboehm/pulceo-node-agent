package dev.pulceo.pna;

import dev.pulceo.pna.exception.JobServiceException;
import dev.pulceo.pna.model.BandwidthJob;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.service.JobService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class JobServiceTests {

    @Autowired
    JobService jobService;

    @BeforeEach
    @AfterEach


    @Test
    public void testCreateBandwidthJob() {
        // given
        BandwidthJob bandwidthJob = new BandwidthJob("localhost", "localhost", 5001, Iperf3ClientProtocol.TCP, 5);

        // when
        long id  = this.jobService.createJob(bandwidthJob);

        // then
        Assertions.assertTrue(id > 0);
    }

    @Test
    public void testScheduleIperf3BandwidthJob() throws IOException, InterruptedException, JobServiceException {
        // given
        int port;
        BandwidthServiceTests.startIperf3ServerInstance(5001);
        long id = jobService.createJob(new BandwidthJob("localhost", "localhost", 5001, Iperf3ClientProtocol.TCP, 5));
        // when
        jobService.scheduleIperf3BandwidthJob(id);

        // then

    }

}
