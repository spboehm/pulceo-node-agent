package dev.pulceo.pna.nping;

import dev.pulceo.pna.model.nping.NpingClientCmd;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NpingCmdTests {

    @Test
    public void testNpingTCPClientCmd() {
        // given
        String cmdTCPClient = "/usr/bin/nping -4 --tcp-connect -c 1 --dest-ip localhost -p 8080 -e eth0";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.TCP,8080,1, "localhost", "eth0").getCmd();

        // then
        assertEquals(cmdTCPClient, npingClientCmd);
    }

    @Test
    public void testNpingUDPClientCmd() {
        // given
        String cmdUDPClient = "/usr/bin/nping -4 --udp -c 1 --dest-ip localhost -p 8080 -e eth0";

        // when
        String npingClientCmd = new NpingClientCmd(NpingClientProtocol.UDP,8080,1, "localhost", "eth0").getCmd();

        // then
        assertEquals(cmdUDPClient, npingClientCmd);
    }

    @Test
    public void getNpingCommandAsList() {
        // given
        List<String> expectedCommandsAsList = Arrays.asList("/usr/bin/nping", "-4", "--udp", "-c", "1", "--dest-ip", "localhost", "-p", "8080", "-e", "eth0");
        NpingClientCmd npingClientCmd = new NpingClientCmd(NpingClientProtocol.UDP,8080,1, "localhost", "eth0");

        // when
        List<String> actualCommandsAsList = npingClientCmd.getNpingCommandAsList();

        // then
        assertEquals(expectedCommandsAsList, actualCommandsAsList);
    }

}
