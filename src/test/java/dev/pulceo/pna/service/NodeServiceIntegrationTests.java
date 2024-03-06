package dev.pulceo.pna.service;

import dev.pulceo.pna.exception.ProcessException;
import dev.pulceo.pna.model.node.Node;
import dev.pulceo.pna.model.node.Storage;
import dev.pulceo.pna.util.ProcessUtils;
import dev.pulceo.pna.util.StorageUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class NodeServiceIntegrationTests {

    @Autowired
    NodeService nodeService;

    @Test
    public void testCreateNode() {
        // given
       Node node = Node.builder()
               .pnaUUID("2f0b7383-4e5c-4392-b74c-6e85a7cfed7a")
               .name("test node")
               .nodeLocationCity("Bamberg")
               .nodeLocationCountry("Germany")
               .pnaEndpoint("http://localhost:7676")
               .host("localhost")
               .build();

        // when
        Node createdNode = this.nodeService.createNode(node);

        // then
        assertEquals(node, createdNode);
    }

    @Test
    public void testIfLocalNodeIsCreatedAfterStartup() {
        // given
        // local node is created with @PostConstruct in NodeService

        // when
        Optional<Node> localNode = this.nodeService.readLocalNode();

        // then
        assert(localNode.isPresent());
        assert (localNode.get().isLocalNode());

    }

    @Test
    public void testObtainStorageInformationForDocker() throws IOException, ProcessException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/storage/df_h_docker_output.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(file)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        Storage storage = StorageUtil.extractStorageInformation(resultList);

        // then
        assertEquals(674.20f, storage.getSize());
        assertEquals(0, storage.getSlots());
    }

    @Test
    public void testObtainStorageInformationForNotebook() throws IOException, ProcessException {
        // given
        File file = new File("src/test/java/dev/pulceo/pna/resources/storage/df_h_notebook_output.txt");
        List<String> resultList;
        try(InputStream inputStream = new FileInputStream(file)) {
            resultList = ProcessUtils.readProcessOutput(inputStream);
        }

        // when
        Storage storage = StorageUtil.extractStorageInformation(resultList);

        // then
        assertEquals(724.0f, storage.getSize());
        assertEquals(0, storage.getSlots());
    }

    // TODO: add read node

}
