package dev.pulceo.pna.service;

import com.google.gson.reflect.TypeToken;
import dev.pulceo.pna.exception.KubernetesServiceException;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.model.application.ApplicationComponentType;
import dev.pulceo.pna.model.node.Node;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.AppsV1Api;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class KubernetesServiceIntegrationTest {

    @Value("${k3s.config.path}")
    private String k3sConfigPath;

    @Value("${k3s.namespace}")
    private String namespace;

    @Autowired
    KubernetesService kubernetesService;

    @Autowired
    NodeService nodeService;

    private CoreV1Api api;
    private AppsV1Api appsV1Api;

    @BeforeEach
    public void setup() throws IOException {
        ApiClient client = Config.fromConfig(k3sConfigPath);
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api();
        this.appsV1Api = new AppsV1Api();
        this.deleteNamespace(this.namespace);
    }

    @AfterEach
    public void tearDown() throws IOException {
        this.deleteNamespace(this.namespace);
    }

    private void deleteNamespace(String namespace) {
        try {
            this.api.deleteNamespace(namespace).execute();
            // TODO: replace with async call
            try (Watch<V1Namespace> watch = Watch.createWatch(
                    // TODO: set timeout
                    this.api.getApiClient(),
                    api.listNamespace()
                            .watch(true)
                            .buildCall(null),
                    new TypeToken<Watch.Response<V1Namespace>>() {}.getType())) {
                for (Watch.Response<V1Namespace> item : watch) {
                    if (Objects.equals(item.object.getMetadata().getName(), namespace) && Objects.equals(item.type, "DELETED")) {
                        break;
                    }
                }
            }
        } catch (ApiException | IOException e) {
            // swallow in case of namespace does not exist
        }
    }

    private void deleteDeployment(String deploymentName, String namespace) {
        try {
            this.appsV1Api.deleteNamespacedDeployment(deploymentName, namespace);
            // TODO: replace with async call
            try (Watch<V1Deployment> watch = Watch.createWatch(
                    // TODO: set timeout
                    this.appsV1Api.getApiClient(),
                    appsV1Api.listNamespacedDeployment(namespace)
                            .watch(true)
                            .buildCall(null),
                    new TypeToken<Watch.Response<V1Deployment>>() {
                    }.getType())) {
                for (Watch.Response<V1Deployment> item : watch) {
                    if (Objects.equals(item.object.getMetadata().getName(), namespace) && Objects.equals(item.type, "DELETED")) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCheckIfNamespaceAlreadyExists() throws KubernetesServiceException {
        // given
        String namespace = "pulceo";
        this.deleteNamespace(namespace);

        // when
        boolean result = this.kubernetesService.checkIfNamespaceAlreadyExists(namespace);

        // then
        assertFalse(result);
    }

    @Test
    public void testCheckIfDeploymentAlreadyExists_whenItDoesNotExist() throws KubernetesServiceException {
        // given
        // no deployment

        // when
        boolean result = this.kubernetesService.isDeploymentExisting(this.namespace, "nginx");

        // then
        assertFalse(result);

    }

    @Test
    public void testCreateNamespaceIfNotExists() throws KubernetesServiceException {
        // given
        String namespace = "pulceo";
        this.deleteNamespace(namespace);

        // when
        this.kubernetesService.createNamespace(namespace);

        // then
        assertTrue(this.kubernetesService.checkIfNamespaceAlreadyExists(namespace));
    }

    @Test
    public void testCreateDeploymentIfNotExists() throws KubernetesServiceException, InterruptedException {
        // given
        Optional<Node> localnode = this.nodeService.readLocalNode();
        this.kubernetesService.createNamespace(this.namespace);

        ApplicationComponent applicationComponent = ApplicationComponent.builder()
                .name("nginx")
                .image("nginx")
                .port(80)
                .protocol("http")
                .applicationComponentType(ApplicationComponentType.PUBLIC)
                .node(localnode.get())
                .build();

        // when
        this.kubernetesService.createDeployment(applicationComponent);

        // then
        // TODO: replace with proper replacement - do not use the implementation in svc
        assertTrue(this.kubernetesService.isDeploymentExisting(this.namespace, "nginx"));
    }

    @Test
    public void testServiceIfNotExists() throws KubernetesServiceException {
        // given
        Optional<Node> localnode = this.nodeService.readLocalNode();
        this.kubernetesService.createNamespace(this.namespace);
        ApplicationComponent applicationComponent = ApplicationComponent.builder()
                .name("nginx")
                .image("nginx")
                .port(80)
                .protocol("http")
                .applicationComponentType(ApplicationComponentType.PUBLIC)
                .node(localnode.get())
                .build();
        this.kubernetesService.createDeployment(applicationComponent);

        // when
        this.kubernetesService.createService(applicationComponent);

        // then
        WebTestClient webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:80").build();
        webTestClient.get().exchange().expectStatus().is2xxSuccessful();

    }
}
