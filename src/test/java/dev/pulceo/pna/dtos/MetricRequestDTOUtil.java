package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestIcmpRttDTO;

import java.util.HashMap;

public class MetricRequestDTOUtil {

        public static CreateNewMetricRequestIcmpRttDTO createIcmpRttMetricRequestDTO(String type) {
            HashMap<String, String> properties = new HashMap<>();
            properties.put("ip-version", "IPv4");
            properties.put("count", "1");
            properties.put("data-length", "66");
            properties.put("iface", "lo");

            return CreateNewMetricRequestIcmpRttDTO.builder()
                    .type(type)
                    .recurrence("15")
                    .enabled(true)
                    .properties(properties)
                    .transformer(new HashMap<>()) // will be ignored for pna
                    .build();
        }

}
