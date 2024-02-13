package dev.pulceo.pna.service;

import dev.pulceo.pna.model.application.Application;
import dev.pulceo.pna.model.application.ApplicationComponent;
import dev.pulceo.pna.model.application.ApplicationComponentProtocol;
import dev.pulceo.pna.model.application.ApplicationComponentType;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.repository.ApplicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ApplicationServiceIntegrationTest {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    NodeService nodeService;

    @Autowired
    ApplicationRepository applicationRepository;

    @Value("{pna.host.fqdn}")
    private String pnaHostFQDN;

    @BeforeEach
    public void setup() {
        this.applicationRepository.deleteAll();
    }

    @Test
    public void testCreateApplicationWithOneComponent() {
        // given
        Optional<Node> localNode = this.nodeService.readLocalNode();
        if (localNode.isEmpty()) {
            throw new RuntimeException("Local node not found");
        }
        Node node = localNode.get();
        ApplicationComponent applicationComponent = ApplicationComponent.builder()
                .name("application-component-nginx")
                .image("nginx")
                .protocol(String.valueOf(ApplicationComponentProtocol.HTTP))
                .port(80)
                .node(node)
                .applicationComponentType(ApplicationComponentType.PUBLIC)
                .build();

        Application application = Application.builder()
                .name("application-nginx")
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
