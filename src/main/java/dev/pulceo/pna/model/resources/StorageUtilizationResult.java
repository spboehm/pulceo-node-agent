package dev.pulceo.pna.model.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.message.MetricResult;
import dev.pulceo.pna.model.message.MetricType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Map;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StorageUtilizationResult extends Resource implements MetricResult {

    private String srcHost;
    private K8sResourceType k8sResourceType;
    private String resourceName;
    private String time;

    @OneToOne(cascade = {CascadeType.ALL})
    private StorageUtilizationMeasurement storageUtilizationMeasurement;

    @Override
    @JsonIgnore
    public UUID getUUID() {
        return super.getUuid();
    }

    @Override
    @JsonIgnore
    public MetricType getMetricType() {
        return MetricType.STORAGE_UTIL;
    }

    @Override
    @JsonIgnore
    public Map<String, Object> getResultData() {
        return Map.of(
                "sourceHost", srcHost,
                "startTime", time,
                "storageUtilizationMeasurement", storageUtilizationMeasurement
        );
    }
}
