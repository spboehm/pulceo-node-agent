package dev.pulceo.pna.ping;

import dev.pulceo.pna.model.ping.IPVersion;
import dev.pulceo.pna.model.ping.PingRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PingRequestsTests {

    @Test
    public void testPingClientCmdIPv4() {
        // given
        String exptectedPingClientCmd = "ping -4 -s 66 -I lo";

        // when
        String actualPingCmd = new PingRequest("localhost", "localhost", IPVersion.IPv4, 66, "lo").getCmd();

        // then
        assertEquals(exptectedPingClientCmd, actualPingCmd);
    }

    @Test
    public void testPingClientCmdIPv6() {
        // given
        String exptectedPingClientCmd = "ping -6 -s 66 -I eth0";

        // when
        String actualPingCmd = new PingRequest("localhost", "localhost", IPVersion.IPv6, 66, "eth0").getCmd();

        // then
        assertEquals(exptectedPingClientCmd, actualPingCmd);
    }
}
