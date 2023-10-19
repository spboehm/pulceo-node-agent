package dev.pulceo.pna.ping;

import dev.pulceo.pna.exception.PingException;
import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.ping.PingDelayMeasurement;
import dev.pulceo.pna.util.PingUtils;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PingUtilsTests {

    @Test
    public void testExtractPingDelayMeasurementWithSuccess() throws ProcessException, IOException, PingException {
        PingDelayMeasurement expectedPingDelayMeasurement = new PingDelayMeasurement(10, 10, 0.000f, 9124, 0.065f, 0.105f, 0.150f, 0.030f);
        extractPingDelayMeasurement(expectedPingDelayMeasurement, "src/test/java/dev/pulceo/pna/resources/ping/ping_4_result_success.txt");
    }

    @Test
    public void testExtractPingDelayMeasurementWithPacketLoss() throws ProcessException, IOException, PingException {
        PingDelayMeasurement expectedPingDelayMeasurement = new PingDelayMeasurement(3, 0, 100.0f, 2030, 0.0f, 0.0f, 0.0f, 0.0f);
        extractPingDelayMeasurement(expectedPingDelayMeasurement, "src/test/java/dev/pulceo/pna/resources/ping/ping_4_result_error_full_packet_loss.txt");
    }

    @Test
    public void testExtractPingDelayMeasurementWithError() throws ProcessException, IOException, PingException {
        // given
        PingDelayMeasurement expecPingDelayMeasurement = new PingDelayMeasurement();
        File pingResult = new File("src/test/java/dev/pulceo/pna/resources/ping/ping_4_result_error_name_not_found.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(pingResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        PingException pingException = assertThrows(PingException.class, () -> {
            PingDelayMeasurement actualPingDelayMeasurement = PingUtils.extractPingDelayMeasurement(resultList);
        });

        // then
        assertEquals("ping: s: Name or service not known", pingException.getMessage());
    }

    private void extractPingDelayMeasurement(PingDelayMeasurement expectedPingDelayMeasurement, String file) throws IOException, ProcessException, PingException {
        // given
        File pingResult = new File(file);
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(pingResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        PingDelayMeasurement actualPingDelayMeasurement = PingUtils.extractPingDelayMeasurement(resultList);

        // then
        assertEquals(expectedPingDelayMeasurement, actualPingDelayMeasurement);
    }

    @Test
    public void testExtractHostFromPingInstance() {
        // given
        String expectedHost = "localhost";
        String cmdPingInstance = "/usr/bin/ping -4 -c 10 -s 66 -I eth0 " + expectedHost;

        // when
        String actualHost = PingUtils.extractHostFromPingCmd(cmdPingInstance);

        // then
        assertEquals(expectedHost, actualHost);
    }
}
