package dev.pulceo.pna.service;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceList;
import io.kubernetes.client.util.Config;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
@Service
public class DeploymentService {

    @PostConstruct
    private void init() throws IOException, ApiException {
        try {
            ApiClient client = Config.fromConfig("/k3s.yaml");
            Configuration.setDefaultApiClient(client);

            CoreV1Api api = new CoreV1Api();

            // TODO: list namespaces

            // TODO: check if namespace PULCEO does exist, otherwise create
            V1ServiceList v1ServiceList = api.listServiceForAllNamespaces(null, null, null, null, null, null, null, null, null, null, false);
            for (V1Service s : v1ServiceList.getItems()) {
                System.out.println(s.getMetadata().getName());
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }


    }

}
