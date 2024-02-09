package dev.pulceo.pna.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.pulceo.pna.model.node.Memory;

import java.util.List;

public class MemoryUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Memory extractMemoryInformation(List<String> procMemInfoAsString) throws JsonProcessingException {
        float size = 0.0f;
        int slots = 0;

        for (String line : procMemInfoAsString) {
            if (line.contains("MemTotal:")) {
                String[] split = line.split(":");
                size = Integer.parseInt(split[1].trim().split(" ")[0]);
            }
        }

        return Memory.builder()
                .size(size / 1024 / 1024)
                .slots(slots)
                .build();
    }
}
