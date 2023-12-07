package dev.pulceo.pna.dto.metricrequests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewMetricRequestIcmpRttDTO {
    private String type;
    private String recurrence;
    private boolean enabled;
    private Map<String, String> properties;
    private Map<String, String> transformer;
}
