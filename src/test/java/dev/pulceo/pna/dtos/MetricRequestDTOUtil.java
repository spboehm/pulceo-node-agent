package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestDTO;

import java.util.HashMap;

public class MetricRequestDTOUtil {

        public static CreateNewMetricRequestDTO createTestMetricRequest(String type) {
            return CreateNewMetricRequestDTO.builder()
                    .type(type)
                    .recurrence("5m")
                    .enabled(true)
                    .properties(new HashMap<>())
                    .transformer(new HashMap<>())
                    .build();
        }

}
