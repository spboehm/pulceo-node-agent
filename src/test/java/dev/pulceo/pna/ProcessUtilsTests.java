package dev.pulceo.pna;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.iperf.IperfServerCmd;
import dev.pulceo.pna.util.ProcessUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class ProcessUtilsTests {

    private String bind = "localhost";

    @BeforeAll
    @AfterAll
    public static void killAllSleepInstances() throws InterruptedException, IOException {
        Process p = new ProcessBuilder("killall", "-e", "sleep").start();
        p.waitFor();
    }

    @Test
    public void testReadProcessOutputFromIperf3Output () throws IOException, ProcessException {
        // given
        File iperf3ClientResult = new File("src/test/java/dev/pulceo/pna/resources/iperf3_tcp_client_result.txt");

        // when
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(iperf3ClientResult)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // then
        Assertions.assertEquals(resultList.size(),19);
    }

    @Test
    public void testReadRunningProcessOutputFromPsOutput () throws ProcessException {
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
        List<String> result = ProcessUtils.getListOfRunningProcessesByName("sleep");

        // then
        // TODO: fix the bug with automatically restarting sleep process
        Assertions.assertTrue(result.size() >= 2);
        Assertions.assertTrue(result.stream().anyMatch(s -> s.contains("sleep") && s.contains("3600") || s.contains("2400")));

    }

    @Test
    public void testSplitCmdByWhitespaces() {
        // given
        int port = 5001;
        IperfServerCmd iperfServerCmd = new IperfServerCmd(port, bind);
        String[] expectedResult = new String[]{"/usr/bin/iperf3", "-s", "-p", String.valueOf(port), "-f", "m", "--bind", "localhost" };
        List<String> expectedResultList = Arrays.asList(expectedResult);

        // when
        List<String> actualResultList = ProcessUtils.splitCmdByWhitespaces(iperfServerCmd.getCmd());

        // then
        Assertions.assertEquals(expectedResultList, actualResultList);
    }


    @ParameterizedTest
    @ValueSource(longs = {32450, 19199, 19233, 4685})
    public void testGetPidOfpsEntry(long expectedPid) {
        // given
        String psEntry = expectedPid + " iperf3   /usr/bin/iperf3 -s -p 5001 -f m --bind localhost";

        // when
        long actualPid = ProcessUtils.getPidOfpsEntry(psEntry);

        // then
        Assertions.assertEquals(expectedPid, actualPid);
    }

    @Test
    public void testGetCmdOfpsEntry() {
        // given
        String psEntry = "19199 iperf3   /usr/bin/iperf3 -s -p 5001 -f m";
        String expectedCmd = "/usr/bin/iperf3 -s -p 5001 -f m";

        // when
        String actualCmd = ProcessUtils.getCmdOfpsEntry(psEntry);

        // then
        Assertions.assertEquals(expectedCmd, actualCmd);
    }

}
