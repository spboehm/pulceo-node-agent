package dev.pulceo.pna.iperf3;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.IperfBandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.IperfClientProtocol;
import dev.pulceo.pna.model.iperf3.IperfRole;
import dev.pulceo.pna.model.iperf3.IperfUDPBandwidthMeasurement;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Iperf3UtilsTests {

    @Test
    public void testExtractIperf3TCPBandwidthMeasurementForSender() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(IperfClientProtocol.TCP, 5.14f, IperfRole.SENDER);
    }

    @Test
    public void testExtractIperfTCP3BandwidthMeasurementForReceiver() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(IperfClientProtocol.TCP,5.14f, IperfRole.RECEIVER);
    }

    @Test
    public void testExtractIperf3UDPBandwidthMeasurementForSender() throws ProcessException, IOException {
        testExtractIperf3UDPBandwidthMeasurement(IperfClientProtocol.UDP, 5.01f, 0.000f, 0, 191, IperfRole.SENDER);
    }

    @Test
    public void testExtractIperfUDP3BandwidthMeasurementForReceiver() throws ProcessException, IOException {
        testExtractIperf3UDPBandwidthMeasurement(IperfClientProtocol.UDP, 5.01f, 0.052f, 0, 191, IperfRole.RECEIVER);
    }

    private void testExtractIperf3BandwidthMeasurement(IperfClientProtocol iperf3Protocol, float bitrate, IperfRole iperfRole) throws IOException, ProcessException {
        // given
        IperfBandwidthMeasurement expectedIperfBandwidthMeasurement = new IperfBandwidthMeasurement(iperf3Protocol, bitrate, iperfRole);

        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_" + iperf3Protocol.toString().toLowerCase() + "_client_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        IperfBandwidthMeasurement actualIperfBandwidthMeasurement = Iperf3Utils.extractIperf3BandwidthMeasurement(iperf3Protocol, resultList, iperfRole);

        // then
        assertEquals(expectedIperfBandwidthMeasurement, actualIperfBandwidthMeasurement);
    }

    private void testExtractIperf3UDPBandwidthMeasurement(IperfClientProtocol iperf3Protocol, float bitrate, float jtter, int lostDatagrams, int totalDatagrams, IperfRole iperfRole) throws IOException, ProcessException {
        // given
        IperfUDPBandwidthMeasurement expectedIperfUDPBandwidthMeasurement = new IperfUDPBandwidthMeasurement(iperf3Protocol, bitrate, iperfRole, jtter, lostDatagrams, totalDatagrams);

        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_" + iperf3Protocol.toString().toLowerCase() + "_client_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        IperfBandwidthMeasurement actualIperfBandwidthMeasurement = Iperf3Utils.extractIperf3UDPBandwidthMeasurement(iperf3Protocol, resultList, iperfRole);

        // then
        assertEquals(expectedIperfUDPBandwidthMeasurement, actualIperfBandwidthMeasurement);
    }

    @Test
    public void testIsReceiver() {
        // given
        String cmdTCPReceiverShort = "/bin/iperf3 -s -p 5001 -f m";
        String cmdTCPReceiverLong = "/bin/iperf3 --server -p 5001 -f m";

        // when
        boolean isReceiverShort = Iperf3Utils.isReceiver(cmdTCPReceiverShort);
        boolean isReceiverLong = Iperf3Utils.isReceiver(cmdTCPReceiverLong);

        // then
        assertTrue(isReceiverShort);
        assertTrue(isReceiverLong);
    }

    @Test
    public void testIsTCPSender() {
        // given
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p 5001 -f m";
        String cmdUDPSenderLong = "/bin/iperf3 --client localhost --udp -f m";
        String cmdTCPSenderShort = "/bin/iperf -c localhost -p 5001 -f m";
        String cmdTCPSenderLong = "/bin/iperf --client localhost -p 5001 -f m";

        // when
        boolean isNotTCPSenderShort = Iperf3Utils.isTCPSender(cmdUDPSenderShort);
        boolean isNotTCPSenderLong = Iperf3Utils.isTCPSender(cmdUDPSenderLong);
        boolean isTCPSenderShort = Iperf3Utils.isTCPSender(cmdTCPSenderShort);
        boolean isTCPSenderLong = Iperf3Utils.isTCPSender(cmdTCPSenderLong);

        // then
        assertFalse(isNotTCPSenderShort);
        assertFalse(isNotTCPSenderLong);
        assertTrue(isTCPSenderShort);
        assertTrue(isTCPSenderLong);
    }

    @Test
    public void testIsUDPSender()  {
        // given
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p 5001 -f m";
        String cmdUDPSenderLong = "/bin/iperf3 --client localhost --udp -f m";
        String cmdTCPSenderShort = "/bin/iperf -c localhost -p 5001 -f m";
        String cmdTCPSenderLong = "/bin/iperf --client localhost -p 5001 -f m";

        // when
        boolean isUDPSenderShort = Iperf3Utils.isUDPSender(cmdUDPSenderShort);
        boolean isUDPSenderLong = Iperf3Utils.isUDPSender(cmdUDPSenderLong);
        boolean isNotUDPSenderShort = Iperf3Utils.isUDPSender(cmdTCPSenderShort);
        boolean isNotUDPSenderLong = Iperf3Utils.isUDPSender(cmdTCPSenderLong);

        // then
        assertTrue(isUDPSenderShort);
        assertTrue(isUDPSenderLong);
        assertFalse(isNotUDPSenderShort);
        assertFalse(isNotUDPSenderLong);
    }


    @Test
    public void testExtractHostFromIperf3UDPClientCmd() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 5001;
        String cmdUDPSenderShort = "/bin/iperf3 -c " + expectedHost + " -u -p " + expectedPort + " -f m";

        // when
        String actualHost = Iperf3Utils.extractHostFromIperf3Cmd(cmdUDPSenderShort);

        // then
        assertEquals(expectedHost, actualHost);
    }

    @Test
    public void testExtractHostFromIperf3TCPClientCmd() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 5001;
        String cmdTCPSenderShort = "/bin/iperf -c " + expectedHost + " -p " + expectedPort + " -f m";

        // when
        String actualHost = Iperf3Utils.extractHostFromIperf3Cmd(cmdTCPSenderShort);

        // then
        assertEquals(expectedHost, actualHost);
    }


    @Test
    public void testExtractPortFromIperf3UDPClientCmd() {
        // given
        int expectedPort = 5001;
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p " + expectedPort + " -f m -t 60";

        // when
        int actualPort = Iperf3Utils.extractPortFromIperf3Cmd(cmdUDPSenderShort);

        // then
        assertEquals(actualPort, expectedPort);
    }

    @Test
    public void testExtractPortFromIperf3TCPClientCmd() {
        // given
        int expectedPort = 5001;
        String cmdTCPSenderShort = "/bin/iperf -c localhost -p " + expectedPort + " -f m -t 60";

        // when
        int actualPort = Iperf3Utils.extractPortFromIperf3Cmd(cmdTCPSenderShort);

        // then
        assertEquals(actualPort, expectedPort);
    }

    @Test
    public void testExtractPortFromIperf3ServerCmd() {
        // given
        int expectedPort = 5001;
        String cmdTCPReceiverShort = "/bin/iperf3 -s -p " + expectedPort + " -f m";

        // when
        int actualPort = Iperf3Utils.extractPortFromIperf3Cmd(cmdTCPReceiverShort);

        // then
        assertEquals(actualPort, expectedPort);
    }

}
