package dev.pulceo.pna;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3Protocol;
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
    void testExtractIperf3BandwidthMeasurementForSender() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(Iperf3Protocol.TCP, 100434,Iperf3Role.SENDER);
    }

    @Test
    void testExtractIperf3BandwidthMeasurementForReceiver() throws ProcessException, IOException {
        testExtractIperf3BandwidthMeasurement(Iperf3Protocol.TCP,100433,Iperf3Role.RECEIVER);
    }

    // TODO: UDP

    void testExtractIperf3BandwidthMeasurement(Iperf3Protocol iperf3Protocol, int bitrate, Iperf3Role iperf3Role) throws IOException, ProcessException {
        // given
        Iperf3BandwidthMeasurement expectedIperf3BandwidthMeasurement = new Iperf3BandwidthMeasurement(iperf3Protocol, bitrate, iperf3Role);

        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_client_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        Iperf3BandwidthMeasurement actualIperf3BandwidthMeasurement = Iperf3Utils.extractBandwidth(iperf3Protocol, resultList, iperf3Role);

        // then
        assertEquals(expectedIperf3BandwidthMeasurement, actualIperf3BandwidthMeasurement);
    }

}
