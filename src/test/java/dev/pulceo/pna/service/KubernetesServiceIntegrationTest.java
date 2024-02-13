package dev.pulceo.pna.service;

import com.google.gson.reflect.TypeToken;
import dev.pulceo.pna.exception.ApplicationServiceException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class KubernetesServiceIntegrationTest {

    @Value("${k3s.config.path}")
    private String k3sConfigPath;

    @Autowired
    KubernetesService kubernetesService;

    private CoreV1Api api;

    @BeforeEach
    public void setup() throws IOException {
        ApiClient client = Config.fromConfig(k3sConfigPath);
        Configuration.setDefaultApiClient(client);
        this.api = new CoreV1Api();
    }

    private void deleteNamespace(String namespace) {
        try {
            this.api.deleteNamespace(namespace).execute();
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

    @Test
    public void testCheckIfNamespaceAlreadyExists() throws ApplicationServiceException, ApiException {
        // given
        String namespace = "pulceo";
        this.deleteNamespace(namespace);

        // when
        boolean result = this.kubernetesService.checkIfNamespaceAlreadyExists(namespace);

        // then
        assertFalse(result);
    }

    @Test
    public void testCreateNamespaceIfNotExists() throws ApplicationServiceException {
        // given
        String namespace = "pulceo";
        this.deleteNamespace(namespace);

        // when
        this.kubernetesService.createNamespace(namespace);

        // then
        assertTrue(this.kubernetesService.checkIfNamespaceAlreadyExists(namespace));
    }



}
