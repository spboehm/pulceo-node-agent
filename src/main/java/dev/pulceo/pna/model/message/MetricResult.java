package dev.pulceo.pna.model.message;

import java.util.Map;
import java.util.UUID;

public interface MetricResult {

    UUID getUUID();

    MetricType getMetricType();

    Map<String, Object> getResultData();

}
