package dev.pulceo.pna.model.resources;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public class ResourceUtilizationRequest extends Resource {

    private ResourceUtilizationType resourceUtilizationType;
    private K8sResourceType k8sResourceType;
    private String resourceName;

}
