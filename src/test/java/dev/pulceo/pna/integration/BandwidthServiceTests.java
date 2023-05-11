package dev.pulceo.pna.integration;

import dev.pulceo.pna.service.BandwidthService;
import org.junit.jupiter.api.Test;

public class BandwidthServiceTests {

    @Test
    void whenIperf3ServerIsCreated_thenIperf3ServerShouldRun() {
        // given
        BandwidthService bandwidthService = new BandwidthService();

        // when
        int port = 5001;
        String result = bandwidthService.createIperf3TCPServer(5001);

        // then
        System.out.println(result);

    }

}
