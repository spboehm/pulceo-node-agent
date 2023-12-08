package dev.pulceo.pna.dtos;

import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestIcmpRttDTO;
import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestTcpRttDto;
import dev.pulceo.pna.dto.metricrequests.CreateNewMetricRequestUdpRttDto;
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

    public static CreateNewMetricRequestUdpRttDto createUdpRttMetricRequestDTO(String type) {
        return CreateNewMetricRequestUdpRttDto.builder()
                .type(type)
                .recurrence("15")
                .rounds(1)
                .build();
    }

    public static CreateNewMetricRequestTcpRttDto createNewMetricRequestTcpRttDto(String type) {
        return CreateNewMetricRequestTcpRttDto.builder()
                .type(type)
                .recurrence("15")
                .rounds(1)
                .build();
    }
}
