package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.resources.ResourceUtilizationRequest;
import dev.pulceo.pna.model.resources.ResourceUtilizationType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResourceUtilizationJob extends NodeJob {

    private ResourceUtilizationType resourceUtilizationType;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ResourceUtilizationRequest resourceUtilizationRequest;
    private int recurrence;

}
