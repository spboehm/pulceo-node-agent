package dev.pulceo.pna.model.application;

import dev.pulceo.pna.model.Resource;
import dev.pulceo.pna.model.node.Node;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.net.URI;
import java.util.Objects;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationComponent extends Resource implements HasEndpoint {

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

    @Override
    public URI getEndpoint() {
        return URI.create(this.getProtocol().toLowerCase() + "://" + this.getNode().getHost() + ":" + this.getPort());
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
