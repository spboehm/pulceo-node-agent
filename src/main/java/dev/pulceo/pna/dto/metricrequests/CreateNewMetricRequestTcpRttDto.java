package dev.pulceo.pna.dto.metricrequests;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewMetricRequestTcpRttDto {
    private String type;
    private String recurrence;
    private boolean enabled;
    // TODO: ipVersion
    @Builder.Default
    private int rounds = 10;
}
