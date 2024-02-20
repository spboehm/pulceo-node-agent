package dev.pulceo.pna.model.application;

import dev.pulceo.pna.dto.application.CreateNewApplicationDTO;
import dev.pulceo.pna.model.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@NamedEntityGraph(
        name = "graph.Application.applicationComponents",
        attributeNodes = {
                @NamedAttributeNode("applicationComponents")
        }
)
public class Application extends Resource {

    private String name;
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.REMOVE }, mappedBy = "application")
    private List<ApplicationComponent> applicationComponents;

    public static Application fromCreateNewApplicationDTO(CreateNewApplicationDTO createNewApplicationDTO) {
        return Application.builder()
                .name(createNewApplicationDTO.getName())
                .applicationComponents(createNewApplicationDTO.getApplicationComponents().stream().map(ApplicationComponent::fromCreateNewApplicationComponentDTO).toList())
                .build();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Application that = (Application) o;

        if (!Objects.equals(name, that.name)) return false;
        return Objects.equals(applicationComponents, that.applicationComponents);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (applicationComponents != null ? applicationComponents.hashCode() : 0);
        return result;
    }

}
