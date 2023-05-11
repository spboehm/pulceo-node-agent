package dev.pulceo.pna.util;

import dev.pulceo.pna.exception.ProcessException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ProcessOutputReader {

    public static List<String> readProcessOutput(Process p) throws ProcessException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(p.getInputStream());
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {

            List<String> result = new ArrayList<>();

            // read process output
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            return result;
        } catch (IOException e) {
            throw new ProcessException("Could not read process output", e);
        }
    }

}
