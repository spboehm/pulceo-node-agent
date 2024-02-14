package dev.pulceo.pna.model.application;

import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;

public interface KubernetesDeployable {

    V1Service getService();
    V1Deployment getDeployment();

}
