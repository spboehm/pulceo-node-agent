package dev.pulceo.pna.model.resources;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class MemoryUtilizationMeasurement extends Resource {

    private String time;
    private long usageBytes;
    private long availableBytes;
    private float usageMemoryPercentage;

}
