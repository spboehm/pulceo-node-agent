package dev.pulceo.pna.model.jobs;

import dev.pulceo.pna.model.ping.PingRequest;
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
public class PingJob extends LinkJob {

    @OneToOne(cascade = CascadeType.ALL)
    private PingRequest pingRequest;
    private int recurrence;
    // default false
    private boolean enabled = false;

    public PingJob(PingRequest pingRequest, int recurrence) {
        this.pingRequest = pingRequest;
        this.recurrence = recurrence;
    }

}

