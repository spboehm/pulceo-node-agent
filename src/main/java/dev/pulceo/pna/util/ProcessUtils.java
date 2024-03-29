package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.ProcessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ProcessUtils {

    public static Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static List<String> readProcessOutput(InputStream inputStream) throws ProcessException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            List<String> processOutput = new ArrayList<>();

            // read process output
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                processOutput.add(line);
            }
            return processOutput;
        } catch (IOException e) {
            throw new ProcessException("Could not read process output", e);
        }
    }

    // TODO: replace with ProcessHandler, this is rather a workaround
    public static List<String> getListOfRunningProcesses() throws ProcessException {
        String tmpFileName = UUID.randomUUID() + ".tmp";
        File tmpProcessOutputFile = new File(tmpFileName);
        Process p = null;
        try {
            ProcessBuilder builder = new ProcessBuilder("ps", "-eo", "pid,fname,cmd");
            builder.redirectOutput(ProcessBuilder.Redirect.to(tmpProcessOutputFile));
            builder.redirectError(ProcessBuilder.Redirect.to(tmpProcessOutputFile));
            p = builder.start();
            p.waitFor();
            List<String> processOutput = Files.readAllLines(Paths.get(tmpFileName));
            boolean filedeleted = tmpProcessOutputFile.delete();
            if (!filedeleted) {
                throw new ProcessException("Could not delete temporary file for reading from process output" + tmpFileName);
            }
            return processOutput;
        } catch (IOException | InterruptedException e) {
            throw new ProcessException("Could not determine list of running processes", e);
        } finally {
            try {
                ProcessUtils.closeProcess(p);
            } catch (IOException e) {
               logger.error("Could not close process", e);
            }
        }
    }

    public static List<String> getListOfRunningProcessesByName(String name) throws ProcessException {
        List<String> listOfRunningProcesses = ProcessUtils.getListOfRunningProcesses();
        List<String> listOfFilteredProcesses = new ArrayList<>();
        for (int i = 0; i < listOfRunningProcesses.size() - 1; i++) {
            if (listOfRunningProcesses.get(i).contains(name)) {
                listOfFilteredProcesses.add(listOfRunningProcesses.get(i));
            }
        }
        return listOfFilteredProcesses;
    }

    public static List<String> splitCmdByWhitespaces(String cmd) {
        return Arrays.asList(cmd.split(" "));
    }

    public static long getPidOfpsEntry (String psEntry) {
        int indexOfPid = 0;
        int indexOfIperf = psEntry.indexOf("iperf");
        return Long.parseLong(psEntry.substring(indexOfPid, indexOfIperf - 1).trim());
    }

    public static String getCmdOfpsEntry(String psEntry) {
        int indexOfCmd = psEntry.indexOf("/");
        return psEntry.substring(indexOfCmd).trim();
    }

    public static long waitUntilProcessIsAlive(Process process) throws InterruptedException, IOException {
        if (process.isAlive()) {
            return process.pid();
        } else {
            Thread.sleep(5000);
            if (process.isAlive()) {
                return process.pid();
            } else {
                logger.error("Process is not alive after waiting for 5 seconds, ...");
                throw new IOException();
            }
        }
    }

    public static void closeProcess(Process process) throws IOException {
        if (process != null) {
            logger.debug("Closing process with PID: " + process.pid());
            process.getInputStream().close();
            process.getOutputStream().close();
            process.getErrorStream().close();
            process.destroy();
            if (process.isAlive()) {
                process.destroyForcibly();
            }
        } else {
            logger.debug("Process is null, nothing to close");
        }
    }
}
