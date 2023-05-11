package dev.pulceo.pna.integration;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.util.ProcessOutputReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessOutputReaderTests {

    @Test
    public void whenReadProcessOutput_thenListofStringsMustBeReturned () throws IOException, InterruptedException, ProcessException {

        //Process iperfServerProcess = new ProcessBuilder("/bin/iperf3", "-s", "-p 9999").start();
        //Process iperfClientProcess = new ProcessBuilder("/bin/iperf3", "-c", "localhost", "-p", "9999").start();
        //iperfClientProcess.waitFor();
        // given

        // when
        //List actualResult = ProcessOutputReader.readProcessOutput(iperfClientProcess);

        // then
//        for (int i = 0; i < actualResult.size(); i++) {
//            System.out.println(actualResult.get(actualResult.size()-4));
//        }

    }

    private void readFromFile() {

    }

}
