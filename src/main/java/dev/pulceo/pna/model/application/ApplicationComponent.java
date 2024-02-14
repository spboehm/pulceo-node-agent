package dev.pulceo.pna.model.application;

import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.node.Node;
import io.kubernetes.client.openapi.models.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationComponent extends Resource implements HasEndpoint, KubernetesDeployable {

    private String name;
    private String image;
    private int port;
    private String protocol;
    private ApplicationComponentType applicationComponentType;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    Application application;
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    private Node node;

    public void addApplication(Application application) {
        this.application = application;
    }

    private String getKubernetesServiceProtocol() {
        if ("UDP".equalsIgnoreCase(this.protocol)) {
            return "UDP";
        } else {
           return "TCP";
        }
    }

    @Override
    public URI getEndpoint() {
        return URI.create(this.getProtocol().toLowerCase() + "://" + this.getNode().getHost() + ":" + this.getPort());
    }

    @Override
    public V1Service getService() {
        V1ServiceBuilder v1ServiceBuilder = new V1ServiceBuilder()
                .withApiVersion("v1")
                .withKind("Service")
                .withMetadata(new V1ObjectMeta().name(name).putLabelsItem("name", name))
                .withSpec(
                        new V1ServiceSpec()
                                .addPortsItem(new V1ServicePort().port(this.port).protocol(this.getKubernetesServiceProtocol()))
                                .putSelectorItem("name", name)
                                .type("LoadBalancer"));
        return v1ServiceBuilder.build();
    }

    @Override
    public V1Deployment getDeployment() {
        V1DeploymentBuilder v1DeploymentBuilder = new V1DeploymentBuilder()
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .withMetadata(new V1ObjectMeta().name(name))
                .withSpec(
                        new V1DeploymentSpec()
                                .replicas(1)
                                .selector(new V1LabelSelector().putMatchLabelsItem("name", name))
                                .template(
                                        new V1PodTemplateSpec()
                                                .metadata(new V1ObjectMeta().putLabelsItem("name", name))
                                                .spec(
                                                        new V1PodSpec()
                                                                .containers(
                                                                        Collections.singletonList(
                                                                                new V1Container()
                                                                                        .name(name)
                                                                                        .image(image)
                                                                                        .ports(List.of(new V1ContainerPort().containerPort(port))))))));
        return v1DeploymentBuilder.build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ApplicationComponent that = (ApplicationComponent) o;

        if (port != that.port) return false;
        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(image, that.image)) return false;
        if (!Objects.equals(protocol, that.protocol)) return false;
        if (applicationComponentType != that.applicationComponentType) return false;
        return Objects.equals(node, that.node);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
        result = 31 * result + (applicationComponentType != null ? applicationComponentType.hashCode() : 0);
        result = 31 * result + (node != null ? node.hashCode() : 0);
        return result;
    }
}
