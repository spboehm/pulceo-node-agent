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

    private MetricResult metricResult;

}
