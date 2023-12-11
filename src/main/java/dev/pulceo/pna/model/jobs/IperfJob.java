package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.iperf.IperfRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IperfJob extends LinkJob {

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iperfRequest_id", referencedColumnName = "id")
    private IperfRequest iperfRequest;
    private int recurrence;

}
