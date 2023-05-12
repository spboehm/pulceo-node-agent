package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.ProcessException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessOutputUtils {

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

}
