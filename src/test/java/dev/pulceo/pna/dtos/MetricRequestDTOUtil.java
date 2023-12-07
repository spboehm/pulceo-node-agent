package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestIcmpRttDTO;

public class MetricRequestDTOUtil {

        public static CreateNewMetricRequestIcmpRttDTO createIcmpRttMetricRequestDTO(String type) {
            return CreateNewMetricRequestIcmpRttDTO.builder()
                    .type(type)
                    .recurrence("15")
                    .enabled(true)
                    .build();
        }

}
