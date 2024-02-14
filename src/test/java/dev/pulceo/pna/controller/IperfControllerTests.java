package dev.pulceo.pna.controller;


import dev.pulceo.pna.repository.JobRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = { "pna.delay.tcp.port=10002", "pna.delay.udp.port=10003", "pna.mqtt.client.id=c5e48d11-e33e-47b2-b2c6-b4a01cc57a33"})
@AutoConfigureMockMvc(addFilters = false)
public class IperfControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    @AfterEach
    public void killAllIperf3Instances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "iperf3").start();
        p.waitFor();
        //this.bandwidthService = new BandwidthService(environment);
    }

    @Test
    public void testCreateNewIperf3Server() throws Exception {
        // when and then
        this.mockMvc.perform(post("/api/v1/iperf3-servers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        // then
    }

    // TODO: add endpoint for deletion

    // TODO: move to internal endpoint

}
