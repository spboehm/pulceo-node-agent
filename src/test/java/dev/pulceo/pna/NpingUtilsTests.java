package dev.pulceo.pna;

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

public class NpingUtilsTests {

    @Test
    public void testExtractNpingDelayMeasurementTCP() throws ProcessException, IOException {
        // given
        // TODO: specify further
        NpingTCPDelayMeasurement expectedNpingTCPDelayMeasurement = new NpingTCPDelayMeasurement(1.407,0.797,1.127,5,5,0,0.00);

        File npingTCPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_tcp_result.txt");
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
    public void testExtractNpingDelayMeasurementUDP() throws ProcessException, IOException {
        // given
        // TODO: specify further
        NpingUDPDelayMeasurement expectedNpingUDPDelayMeasurement = new NpingUDPDelayMeasurement(0.447,0.301,0.362,5,5,0,0.00);

        File npingUDPResult = new File("src/test/java/dev/pulceo/pna/resources/nping/nping_udp_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(npingUDPResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        NpingUDPDelayMeasurement actualNpingUDPDelayMeasurement = NpingUtils.extractNpingUDPDelayMeasurement(NpingClientProtocol.UDP, resultList);

        // then
        Assertions.assertEquals(expectedNpingUDPDelayMeasurement, actualNpingUDPDelayMeasurement);
    }

}
