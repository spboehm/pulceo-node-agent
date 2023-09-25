package dev.pulceo.pna.nping;

import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NpingRequestsTests {

    @Test
    public void testNpingTCPClientCmd() {
        // given
        String expectedNpingClientCmd = "/usr/bin/nping -4 --tcp-connect -c 1 --dest-ip localhost -p 8080 -e eth0";

        // when
        String actualNpingClientCmd = new NpingRequest("localhost", "localhost", 8080, NpingClientProtocol.TCP, 1, "eth0").getCmd();

        // then
        assertEquals(expectedNpingClientCmd, actualNpingClientCmd);
    }

    @Test
    public void testNpingUDPClientCmd() {
        // given
        String expectedNpingClientCmd = "/usr/bin/nping -4 --udp -c 1 --dest-ip localhost -p 8080 -e eth0";

        // when
        String actualNpingClientCmd = new NpingRequest("localhost", "localhost", 8080, NpingClientProtocol.UDP, 1, "eth0").getCmd();

        // then
        assertEquals(expectedNpingClientCmd, actualNpingClientCmd);
    }

}
