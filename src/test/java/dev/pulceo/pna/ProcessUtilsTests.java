package dev.pulceo.pna;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.List;

public class ProcessUtilsTests {

    @BeforeAll
    @AfterAll
    public static void killAllSleepInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "sleep").start();
        p.waitFor();
    }

    @Test
    public void testReadProcessOutputFromIperf3Output () throws IOException, ProcessException {
        // given
        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_client_result.txt");

        // when
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // then
        Assertions.assertEquals(resultList.size(),19);
    }

    @Test
    public void testReadRunningProcessOutputFromPsOutput () throws ProcessException, IOException {
        // given
        String expectedFirstColumn = "PID";
        String expectedSecondColumn = "COMMAND";
        String expectedThirdColumn = "CMD";

        // when
        List<String> resultList = ProcessUtils.getListOfRunningProcesses();

        // then
        Assertions.assertTrue(resultList.size() > 0);
        Assertions.assertTrue(resultList.get(0).contains(expectedFirstColumn));
        Assertions.assertTrue(resultList.get(0).contains(expectedSecondColumn));
        Assertions.assertTrue(resultList.get(0).contains(expectedThirdColumn));
    }

    @Test
    public void testGetRunningProcessesByName () throws ProcessException, IOException, InterruptedException {
        // given
        Process sleepProcessOne = new ProcessBuilder("/bin/sleep", "3600").start();
        Process sleepProcessThree = new ProcessBuilder("/bin/sleep", "2400").start();

        while (!sleepProcessOne.isAlive() && !sleepProcessThree.isAlive()) {
            Thread.sleep(1000);
        }

        // when
        List<String> result = ProcessUtils.getRunningProcessesByName("sleep");

        // then
        Assertions.assertTrue(result.size() == 2);
        Assertions.assertTrue(result.get(0).contains("sleep"));
        Assertions.assertTrue(result.get(0).contains("3600"));
        Assertions.assertTrue(result.get(1).contains("sleep"));
        Assertions.assertTrue(result.get(1).contains("2400"));
    }

}
