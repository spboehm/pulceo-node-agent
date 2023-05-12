package dev.pulceo.pna;

import dev.pulceo.pna.exception.SubProcessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProcessOutputUtils {

    @Test
    public void testReadProcessOutputFromIperf3Output () throws IOException, SubProcessException {
        // given
        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_client_result.txt");

        // when
        List<String> resultList = new ArrayList<>();
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = dev.pulceo.pna.util.ProcessOutputUtils.readProcessOutput(inputStream);
        }

        // then
        Assertions.assertEquals(resultList.size(),19);
    }

}
