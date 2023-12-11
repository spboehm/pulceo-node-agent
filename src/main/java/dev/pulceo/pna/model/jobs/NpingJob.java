package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.nping.NpingRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NpingJob extends LinkJob {

    @OneToOne(cascade = CascadeType.ALL)
    private NpingRequest npingRequest;
    private int recurrence;

}
