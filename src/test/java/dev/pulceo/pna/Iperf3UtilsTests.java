package dev.pulceo.pna;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf3.Iperf3BandwidthMeasurement;
import dev.pulceo.pna.model.iperf3.Iperf3Role;
import dev.pulceo.pna.util.Iperf3Utils;
import dev.pulceo.pna.util.ProcessOutputUtils;
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
        testExtractIperf3BandwidthMeasurement(100434,Iperf3Role.SENDER);
        testExtractIperf3BandwidthMeasurement(100433,Iperf3Role.RECEIVER);
    }


    void testExtractIperf3BandwidthMeasurement(int bitrate, Iperf3Role iperf3Role) throws IOException, ProcessException {
        // given
        Iperf3BandwidthMeasurement expectedIperf3BandwidthMeasurement = new Iperf3BandwidthMeasurement(bitrate, iperf3Role);

        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_client_result.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessOutputUtils.readProcessOutput(inputStream);
        }

        // when
        Iperf3BandwidthMeasurement actualIperf3BandwidthMeasurement = Iperf3Utils.extractBandwidth(resultList, iperf3Role);

        // then
        assertEquals(expectedIperf3BandwidthMeasurement, actualIperf3BandwidthMeasurement);
    }

}
