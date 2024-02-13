package dev.pulceo.pna.service;

import com.google.gson.reflect.TypeToken;
import dev.pulceo.pna.exception.ApplicationServiceException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class KubernetesService {

    @Value("${k3s.config.path}")
    private String k3sConfigPath;

    public void createNamespace(String namespace) throws ApplicationServiceException {
        try {
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            V1Namespace v1Namespace = new V1Namespace();
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(namespace);
            v1Namespace.setMetadata(v1ObjectMeta);

            api.createNamespace(v1Namespace).execute();

            try {
                try (Watch<V1Namespace> watch = Watch.createWatch(
                        api.getApiClient(),
                        api.listNamespace()
                                .watch(true)
                                .buildCall(null),
                        new TypeToken<Watch.Response<V1Namespace>>() {}.getType())) {
                    for (Watch.Response<V1Namespace> item : watch) {
                        // TODO: add specific search for created namespace
                        if (Objects.equals(Objects.requireNonNull(item.object.getMetadata()).getName(), namespace) && Objects.equals(item.type, "ADDED")) {
                            break;
                        }
                    }
                }
            } catch (ApiException | IOException e) {
                System.err.println(e.getMessage());
            }

        } catch (IOException | ApiException e) {
            throw new ApplicationServiceException(e.getMessage(), e);
        }
    }

    public boolean checkIfNamespaceAlreadyExists(String namespace) throws ApplicationServiceException {
        try {
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            V1NamespaceList v1NamespaceList = api.listNamespace().execute();
            List<String> namespaces = v1NamespaceList.getItems().stream()
                    .map(V1Namespace::getMetadata)
                    .filter(Objects::nonNull)
                    .map(V1ObjectMeta::getName)
                    .filter(namespace::equals)
                    .toList();
            return !namespaces.isEmpty();
        } catch (ApiException | IOException e) {
            throw new ApplicationServiceException(e.getMessage(), e);
        }
    }

    @PostConstruct
    private void init() {
        try {
            File file = new File(k3sConfigPath);
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            // TODO: create namespace if not exists

        } catch (Exception e) {
        }


    }
}
