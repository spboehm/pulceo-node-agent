package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.resources.ResourceUtilizationRequest;
import dev.pulceo.pna.model.resources.ResourceUtilizationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// for node
public class ResourceUtilizationJob extends NodeJob {

    private ResourceUtilizationType resourceUtilizationType;
    @OneToOne(cascade = CascadeType.ALL)
    private ResourceUtilizationRequest resourceUtilizationRequest;
    private int recurrence;

}
