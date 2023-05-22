package dev.pulceo.pna;

import dev.pulceo.pna.model.iperf3.IperfClientCmd;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfServerCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Iperf3CmdTests {

    @Test
    public void testIperf3ServerCmd() {
        // given
        String cmdTCPReceiverShort = "/bin/iperf3 -s -p 5001 -f m";

        // when
        String iperf3ServerCmd = new IperfServerCmd(5001).getCmd();

        // then
        Assertions.assertEquals(cmdTCPReceiverShort, iperf3ServerCmd);
    }

    @Test
    public void testIperf3UDPClientCmd() {
        // given
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p 5001 -f m";

        // when
        String iperf3ClientCmd = new IperfClientCmd("localhost", 5001, IperfClientProtocol.UDP).getCmd();

        // then
        Assertions.assertEquals(cmdUDPSenderShort, iperf3ClientCmd);
    }

    @Test
    public void testIperf3TCPClientCmd() {
        // given
        String cmdTCPSenderShort = "/bin/iperf3 -c localhost -p 5001 -f m";

        // when
        String iperf3ClientCmd = new IperfClientCmd("localhost", 5001, IperfClientProtocol.TCP).getCmd();

        // then
        Assertions.assertEquals(cmdTCPSenderShort, iperf3ClientCmd);
    }

}
