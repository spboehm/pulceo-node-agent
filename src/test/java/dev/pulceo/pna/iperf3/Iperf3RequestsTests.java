package dev.pulceo.pna.iperf3;

import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfRequest;
import dev.pulceo.pna.model.iperf3.IperfServerCmd;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class Iperf3RequestsTests {

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
        String expectedIperfUDPClientCmd = "/bin/iperf3 -c localhost -u -p 5001 -b 1M -t 1 -f m";

        // when
        String actualIperfTCPClientCmd = new IperfRequest("localhost", "localhost", 5001, 1, 1, IperfClientProtocol.UDP).getCmd();

        // then
        Assertions.assertEquals(expectedIperfUDPClientCmd, actualIperfTCPClientCmd);
    }

    @Test
    public void testIperf3TCPClientCmd() {
        // given
        String expectedIperfTCPClientCmd = "/bin/iperf3 -c localhost -p 5001 -b 0M -t 1 -f m";

        // when
        String actualIperfTCPClientCmd = new IperfRequest("localhost", "localhost", 5001, 0, 1, IperfClientProtocol.TCP).getCmd();

        // then
        Assertions.assertEquals(expectedIperfTCPClientCmd, actualIperfTCPClientCmd);
    }

}
