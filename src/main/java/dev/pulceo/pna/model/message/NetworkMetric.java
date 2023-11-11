package dev.pulceo.pna.model.message;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NetworkMetric extends Metric {

//    private UUID linkUUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    private MetricResult metricResult;

}
