package dev.pulceo.pna.nping;

import dev.pulceo.pna.model.nping.NpingClientCmd;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NpingCmdTests {

    @Test
    public void testNpingTCPClientCmd() {
        // given
        String cmdTCPClient = "/usr/bin/nping --tcp-connect -p 8080 -c 20 localhost -e eth0";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.TCP,8080,20, "localhost", "eth0").getCmd();

        // then
        Assertions.assertEquals(cmdTCPClient, npingClientCmd);
    }

    @Test
    public void testIperf3UDPClientCmd() {
        // given
        String cmdUDPClient = "/usr/bin/nping --udp -p 8080 -c 20 localhost -e eth0";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.UDP,8080,20, "localhost", "eth0").getCmd();

        // then
        Assertions.assertEquals(cmdUDPClient, npingClientCmd);
    }

}
