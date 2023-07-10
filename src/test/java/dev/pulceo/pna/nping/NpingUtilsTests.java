package dev.pulceo.pna.nping;

import dev.pulceo.pna.exception.NpingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.nping.NpingClientProtocol;
import dev.pulceo.pna.model.nping.NpingTCPDelayMeasurement;
import dev.pulceo.pna.model.nping.NpingUDPDelayMeasurement;
import dev.pulceo.pna.util.NpingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NpingUtilsTests {

    @Test
    public void testExtractSuccessfulNpingDelayMeasurementTCP() throws ProcessException, IOException, NpingException {
        // given
        NpingTCPDelayMeasurement expectedNpingTCPDelayMeasurement = new NpingTCPDelayMeasurement(1.156,0.810,0.950,10,10,0,0.00);

        File npingTCPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_tcp_result_success.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(npingTCPResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        NpingTCPDelayMeasurement actualNpingTCPDelayMeasurement = NpingUtils.extractNpingTCPDelayMeasurement(NpingClientProtocol.TCP, resultList);

        // then
        Assertions.assertEquals(expectedNpingTCPDelayMeasurement,actualNpingTCPDelayMeasurement);
    }

    @Test
    public void testExtractNotSuccessfulNpingDelayMeasurementTCP() throws ProcessException, IOException, NpingException {
        // given
        File npingUDPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_tcp_result_error.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(npingUDPResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        NpingException npingException = assertThrows(NpingException.class, () -> {
            NpingUtils.extractNpingTCPDelayMeasurement(NpingClientProtocol.UDP, resultList);
        });

        // then
        assertEquals("Could not obtain nping TCP delay mesurement!", npingException.getMessage());
    }

    @Test
    public void testExtractSuccessfulNpingDelayMeasurementUDP() throws ProcessException, IOException, NpingException {
        // given
        NpingUDPDelayMeasurement expectedNpingUDPDelayMeasurement = new NpingUDPDelayMeasurement(0.749,0.335,0.501,10,10,0,0.00);

        File npingUDPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_udp_result_success.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(npingUDPResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        NpingUDPDelayMeasurement actualNpingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, resultList);

        // then
        Assertions.assertEquals(expectedNpingUDPDelayMeasurement, actualNpingUDPDelayMeasurement);
    }

    @Test
    public void testExtractNotSuccessfulNpingDelayMeasurementUDP() throws ProcessException, IOException, NpingException {
        // given
        File npingUDPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_udp_result_error.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(npingUDPResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        NpingException npingException = assertThrows(NpingException.class, () -> {
            NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, resultList);
        });

        // then
        assertEquals("Could not obtain nping UDP delay mesurement!", npingException.getMessage());
    }

    @Test
    public void testIsUDPInstance() {
        // given
        String npingUDPCmd = "/usr/bin/nping -4 --udp -c 20 localhost -p 8080 --data-length 66 -e eth0";

        // when
        boolean isUDPInstance = NpingUtils.isUDP(npingUDPCmd);

        // then
        assertTrue(isUDPInstance);
    }

    @Test
    public void testIsTCP() {
        // given
        String npingTCPCmd = "/usr/bin/nping -4 --tcp-connect -c 20 localhost -p 8080 -e eth0";

        // when
        boolean isTCPInstance = NpingUtils.isTCP(npingTCPCmd);

        // then
        assertTrue(isTCPInstance);
    }

    @Test
    public void testExtractHostFromNpingUDPInstance() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 4001;
        String cmdUDPInstance = "/usr/bin/nping -4 --udp -c 20 --dest-ip" + expectedHost + " -p " + expectedPort + " -e eth0 --data-length 66";

        // when
        String actualHost = NpingUtils.extractHostFromNpingCmd(cmdUDPInstance);

        // then
        assertEquals(expectedHost, actualHost);
    }

    @Test
    public void testExtractHostFromNpingTCPInstance() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 4001;
        String cmdTCPInstance = "/usr/bin/nping -4 --tcp-connect -c 20 --dest-ip" + expectedHost + " -p " + expectedPort + " -e eth0";

        // when
        String actualHost = NpingUtils.extractHostFromNpingCmd(cmdTCPInstance);

        // then
        assertEquals(expectedHost, actualHost);
    }

    @Test
    public void testExtractPortFromNpingUDPInstance() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 4001;
        String cmdUDPInstance = "/usr/bin/nping -4 --udp -c 20 --dest-ip" + expectedHost + " -p " + expectedPort + " -e eth0 --data-length 66";

        // when
        int actualPort = NpingUtils.extractPortFromNpingCmd(cmdUDPInstance);

        // then
        assertEquals(actualPort, expectedPort);
    }

    @Test
    public void testExtractPortFromNpingTCPInstance() {
        // given
        String expectedHost = "localhost";
        int expectedPort = 4001;
        String cmdTCPInstance = "/usr/bin/nping -4 --tcp-connect -c 20 --dest-ip" + expectedHost + " -p " + expectedPort + " -e eth0";

        // when
        int actualPort = NpingUtils.extractPortFromNpingCmd(cmdTCPInstance);

        // then
        assertEquals(expectedPort, actualPort);
    }

}
