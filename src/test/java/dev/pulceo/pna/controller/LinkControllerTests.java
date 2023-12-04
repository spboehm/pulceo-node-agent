package dev.pulceo.pna.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class LinkControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testNewICMPRTTRequest() {
        // given
        // TODO: create two nodes


        // TODO: create a Link between the two nodes

        // TOOD: create a MetricRequestDTO for IMCP-RTT

        // when

        // then
    }


}
