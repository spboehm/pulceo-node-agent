package dev.pulceo.pna;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

}
