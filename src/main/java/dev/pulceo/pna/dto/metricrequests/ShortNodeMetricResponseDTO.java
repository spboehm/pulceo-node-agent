package dev.pulceo.pna.dto.metricrequests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortNodeMetricResponseDTO {
    private UUID remoteMetricRequestUUID;
    private UUID remoteNodeUUID; // local on device
    private String type;
    private String recurrence;
    private boolean enabled;
}
