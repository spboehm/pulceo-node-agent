package dev.pulceo.pna.ping;

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

public class PingUtilsTests {

    @Test
    public void testExtractPingDelayMeasurement() throws IOException, ProcessException {
        // given
        PingDelayMeasurement expectedPingDelayMeasurement = new PingDelayMeasurement(10, 10, 0.000f, 9124, 0.065f, 0.105f, 0.150f, 0.030f);

        File pingResult = new File("src/test/java/dev/pulceo/pna/resources/ping/ping_4_result_success.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(pingResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        PingDelayMeasurement actualPingDelayMeasurement = PingUtils.extractPingDelayMeasurement(resultList);

        // then
        assertEquals(expectedPingDelayMeasurement, actualPingDelayMeasurement);
    }


}
