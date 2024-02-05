package dev.pulceo.pna.dto.metricrequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortMetricResponseDTO {
    private UUID remoteMetricRequestUUID;
    private UUID remoteLinkUUID; // local on device
    private String type;
    private String recurrence;
    private boolean enabled;
}
