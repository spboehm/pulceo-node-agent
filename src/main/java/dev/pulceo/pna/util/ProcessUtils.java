package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.ProcessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

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

    public static List<String> getListOfRunningProcesses() throws ProcessException {
        try {
            Process p = new ProcessBuilder("ps", "-eo", "pid,fname,cmd").start();
            p.waitFor();
            return ProcessUtils.readProcessOutput(p.getInputStream());
        } catch (IOException | InterruptedException | ProcessException e) {
            throw new ProcessException("Could not determine list of running processes", e);
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

}
