package dev.pulceo.pna.model.resources;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StorageUtilizationMeasurement extends Resource {

    private String time;
    private String name;
    private long usedBytes;
    private long capacityBytes;
    private float usageStoragePercentage;

}
