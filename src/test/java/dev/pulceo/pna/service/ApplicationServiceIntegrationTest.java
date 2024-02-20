package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ApplicationServiceException;
import dev.pulceo.pna.exception.KubernetesServiceException;
import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.model.application.ApplicationComponentProtocol;
import dev.pulceo.pna.model.application.ApplicationComponentType;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.ApplicationRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationServiceIntegrationTest {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    NodeService nodeService;

    @Autowired
    KubernetesService kubernetesService;

    @Autowired
    ApplicationRepository applicationRepository;

    @Value("{pna.host.fqdn}")
    private String pnaHostFQDN;

    @BeforeEach
    public void setup() throws KubernetesServiceException {
        applicationRepository.deleteAll();
        kubernetesService.deleteNamespace("pulceo");
        kubernetesService.createNamespace("pulceo");

    }

    @AfterAll
    public void tearDown() {
        applicationRepository.deleteAll();
        kubernetesService.deleteNamespace("pulceo");
    }

    @Test
    public void testCreateApplication() throws ApplicationServiceException {
        // given
        Optional<Node> localNode = this.nodeService.readLocalNode();
        if (localNode.isEmpty()) {
            throw new RuntimeException("Local node not found");
        }
        Application application = Application.builder()
                .name("app-nginx2")
                .applicationComponents(new ArrayList<>())
                .build();

        // when
        Application createdApplication = this.applicationService.createApplication(application);

        // then
        assertEquals(application, createdApplication);
    }

    @Test
    public void testCreateApplicationWithOneComponent() throws ApplicationServiceException {
        // given
        Optional<Node> localNode = this.nodeService.readLocalNode();
        if (localNode.isEmpty()) {
            throw new RuntimeException("Local node not found");
        }
        Node node = localNode.get();
        ApplicationComponent applicationComponent = ApplicationComponent.builder()
                .name("component-nginx")
                .image("nginx")
                .protocol(String.valueOf(ApplicationComponentProtocol.HTTP))
                .port(80)
                .node(node)
                .applicationComponentType(ApplicationComponentType.PUBLIC)
                .environmentVariables(Map.ofEntries(
                        Map.entry("TEST", "TEST")
                ))
                .build();

        Application application = Application.builder()
                .name("app-nginx")
                .applicationComponents(List.of(applicationComponent))
                .build();

        applicationComponent.addApplication(application);

        // when
        Application createdApplication = this.applicationService.createApplication(application);

        // then
        assertEquals(application, createdApplication);
        assertEquals("http://127.0.0.1:80", createdApplication.getApplicationComponents().get(0).getEndpoint().toString());
    }


}
