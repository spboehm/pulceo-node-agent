package dev.pulceo.pna.model.application;

import io.kubernetes.client.openapi.models.V1Deployment;

public interface KubernetesDeployable {

    String getService();
    V1Deployment getDeployment();

}
