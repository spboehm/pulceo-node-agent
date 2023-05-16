package dev.pulceo.pna;

import dev.pulceo.pna.model.iperf3.Iperf3ClientCmd;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.model.iperf3.Iperf3ServerCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Iperf3CmdTests {

    @Test
    public void testIperf3ServerCmd() {
        // given
        String cmdTCPReceiverShort = "/bin/iperf3 -s -p 5001 -f m";

        // when
        String iperf3ServerCmd = new Iperf3ServerCmd(5001).getCmd();

        // then
        Assertions.assertEquals(cmdTCPReceiverShort, iperf3ServerCmd);
    }

    @Test
    public void testIperf3UDPClientCmd() {
        // given
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p 5001 -f m";

        // when
        String iperf3ClientCmd = new Iperf3ClientCmd("localhost", 5001, Iperf3ClientProtocol.UDP).getCmd();

        // then
        Assertions.assertEquals(cmdUDPSenderShort, iperf3ClientCmd);
    }

    @Test
    public void testIperf3TCPClientCmd() {
        // given
        String cmdTCPSenderShort = "/bin/iperf3 -c localhost -p 5001 -f m";

        // when
        String iperf3ClientCmd = new Iperf3ClientCmd("localhost", 5001, Iperf3ClientProtocol.TCP).getCmd();

        // then
        Assertions.assertEquals(cmdTCPSenderShort, iperf3ClientCmd);
    }

}
