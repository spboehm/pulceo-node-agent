package dev.pulceo.pna.nping;

import dev.pulceo.pna.model.nping.NpingClientCmd;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NpingCmdTests {

    @Test
    public void testNpingTCPClientCmd() {
        // given
        String cmdTCPClient = "/usr/bin/nping -4 --tcp-connect -c 20 --dest-ip localhost -p 8080 -e eth0";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.TCP,8080,20, "localhost", "eth0").getCmd();

        // then
        Assertions.assertEquals(cmdTCPClient, npingClientCmd);
    }

    @Test
    public void testIperf3UDPClientCmd() {
        // given
        String cmdUDPClient = "/usr/bin/nping -4 --udp -c 20 --dest-ip localhost -p 8080 -e eth0 --data-length 66";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.UDP,8080,20, "localhost", "eth0").getCmd();

        // then
        Assertions.assertEquals(cmdUDPClient, npingClientCmd);
    }

}
