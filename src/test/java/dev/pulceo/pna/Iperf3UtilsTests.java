package dev.pulceo.pna;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3ClientProtocol;
import dev.pulceo.pna.model.iperf3.Iperf3Role;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class Iperf3UtilsTests {

    @Test
    public void testExtractIperf3BandwidthMeasurementForSender() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(Iperf3ClientProtocol.TCP, 100434, Iperf3Role.SENDER);
    }

    @Test
    public void testExtractIperf3BandwidthMeasurementForReceiver() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(Iperf3ClientProtocol.TCP,100433, Iperf3Role.RECEIVER);
    }
    // TODO: UDP

    private void testExtractIperf3BandwidthMeasurement(Iperf3ClientProtocol iperf3Protocol, int bitrate, Iperf3Role iperf3Role) throws IOException, ProcessException {
        // given
        Iperf3BandwidthMeasurement expectedIperf3BandwidthMeasurement = new Iperf3BandwidthMeasurement(iperf3Protocol, bitrate, iperf3Role);

        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_client_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        Iperf3BandwidthMeasurement actualIperf3BandwidthMeasurement = Iperf3Utils.extractIperf3BandwidthMeasurement(iperf3Protocol, resultList, iperf3Role);

        // then
        assertEquals(expectedIperf3BandwidthMeasurement, actualIperf3BandwidthMeasurement);
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
    public void testExtractPortFromIperf3Cmd() {
        // given
        int expectedPort = 5001;
        String cmdUDPSenderShort = "/bin/iperf3 -c localhost -u -p " + expectedPort + " -f m";

        // when
        int actualPort = Iperf3Utils.extractPortFromIperf3Cmd(cmdUDPSenderShort);

        // then
        assertEquals(actualPort, expectedPort);
    }

}
