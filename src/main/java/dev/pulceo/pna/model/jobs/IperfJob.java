package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.iperf.IperfRequest;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IperfJob extends Job {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iperfRequest_id", referencedColumnName = "id")
    private IperfRequest iperfRequest;
    private int recurrence;
    // default false
    private boolean enabled = false;

    public IperfJob(IperfRequest iperfRequest, int recurrence) {
        this.iperfRequest = iperfRequest;
        this.recurrence = recurrence;
    }
}
