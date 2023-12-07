package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestDTO;

import java.util.HashMap;

public class MetricRequestDTOUtil {

        public static CreateNewMetricRequestDTO createIcmpRttMetricRequestDTO(String type) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("ip-version", "IPv4");
            properties.put("count", "1");
            properties.put("data-length", "66");
            properties.put("iface", "lo");

            return CreateNewMetricRequestDTO.builder()
                    .type(type)
                    .recurrence("15")
                    .enabled(true)
                    .properties(properties)
                    .transformer(new HashMap<>()) // will be ignored for pna
                    .build();
        }

}
