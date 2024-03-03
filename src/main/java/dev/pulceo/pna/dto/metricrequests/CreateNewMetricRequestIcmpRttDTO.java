package dev.pulceo.pna.dto.metricrequests;

import dev.pulceo.pna.model.ping.IPVersion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewMetricRequestIcmpRttDTO {
    private String type;
    private String recurrence;
    private boolean enabled;
    @Builder.Default
    private IPVersion ipVersion = IPVersion.IPv4;
    @Builder.Default
    private int count = 10;
    @Builder.Default
    private int dataLength = 66;
    // TODO: remove
    @Builder.Default
    private String iface = "eth0";
}
