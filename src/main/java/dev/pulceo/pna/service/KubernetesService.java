package dev.pulceo.pna.service;

import com.google.gson.reflect.TypeToken;
import dev.pulceo.pna.exception.KubernetesServiceException;
import dev.pulceo.pna.model.application.KubernetesDeployable;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.wait.Wait;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class KubernetesService {

    @Value("${k3s.config.path}")
    private String k3sConfigPath;

    @Value("${k3s.namespace}")
    private String namespace;

    public void createNamespace(String namespace) throws KubernetesServiceException {
        try {
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            V1Namespace v1Namespace = new V1Namespace();
            V1ObjectMeta v1ObjectMeta = new V1ObjectMeta();
            v1ObjectMeta.setName(namespace);
            v1Namespace.setMetadata(v1ObjectMeta);

            api.createNamespace(v1Namespace).execute();
            // TODO: replace with AsyncCall
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
            throw new KubernetesServiceException("Could not create new namespace!", e);
        }
    }

    public void createDeployment(KubernetesDeployable kubernetesDeployable) throws KubernetesServiceException {
        // TODO: check if deployment does already exist!

        try {
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            AppsV1Api appsV1Api = new AppsV1Api(client);

            V1Deployment v1Deployment = appsV1Api.createNamespacedDeployment(this.namespace, kubernetesDeployable.getDeployment()).execute();

            // Wait until example deployment is ready
            // TODO: replace with async callback
            Wait.poll(
                    Duration.ofSeconds(3),
                    Duration.ofSeconds(60),
                    () -> {
                        try {
                            return appsV1Api
                                    .readNamespacedDeployment(kubernetesDeployable.getDeployment().getMetadata().getName(), namespace)
                                    .execute()
                                    .getStatus()
                                    .getReadyReplicas() > 0;
                        } catch (ApiException e) {
                            return false;
                        }
                    });
        } catch (IOException | ApiException e) {
            throw new KubernetesServiceException("Could not create deployment!", e);
        }
    }

    // TODO: simplify
    public boolean checkIfNamespaceAlreadyExists(String namespace) throws KubernetesServiceException {
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
            throw new KubernetesServiceException("Could not check for already existing namespaces!", e);
        }
    }

    public boolean isDeploymentExisting(String namespace, String deploymentName) throws KubernetesServiceException {
        try {
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            AppsV1Api appsV1Api = new AppsV1Api();

            V1Deployment deploymentV1 = appsV1Api.readNamespacedDeployment(deploymentName, namespace).execute();
            return true;
        } catch (ApiException | IOException e) {
            // only for ApiException
            return false;
        }
    }

    @PostConstruct
    private void init() throws KubernetesServiceException {
        try {
            File file = new File(k3sConfigPath);
            ApiClient client = Config.fromConfig(k3sConfigPath);
            Configuration.setDefaultApiClient(client);
            CoreV1Api api = new CoreV1Api();

            // TODO: create namespace if not exists
            if (!checkIfNamespaceAlreadyExists(this.namespace)) {
                this.createNamespace(this.namespace);
            }

        } catch (Exception e) {
            throw new KubernetesServiceException("Could not init KubernetesService!", e);
        }
    }
}
