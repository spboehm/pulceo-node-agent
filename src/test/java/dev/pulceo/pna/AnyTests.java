package dev.pulceo.pna;

import dev.pulceo.pna.service.AnyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AnyTests {

    @Autowired
    AnyService anyService;

    @Test
    public void testSendZeroMQMessaging() {
        anyService.test();

    }

}
