package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestIcmpRttDTO;
import dev.pulceo.pna.model.ping.IPVersion;

public class MetricRequestDTOUtil {

        public static CreateNewMetricRequestIcmpRttDTO createIcmpRttMetricRequestDTO(String type) {
            return CreateNewMetricRequestIcmpRttDTO.builder()
                    .type(type)
                    .recurrence("15")
                    .enabled(true)
                    .ipVersion(IPVersion.IPv4)
                    .count(1)
                    .dataLength(66)
                    .build();
        }

}
