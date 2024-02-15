package dev.pulceo.pna.model.resources;

import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.message.MetricResult;
import dev.pulceo.pna.model.message.MetricType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.minidev.json.annotate.JsonIgnore;

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
    public MetricType getMetricType() {
        return null;
    }

    @Override
    public Map<String, Object> getResultData() {
        return null;
    }
}
