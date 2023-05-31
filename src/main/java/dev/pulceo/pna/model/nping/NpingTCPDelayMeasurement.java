package dev.pulceo.pna.model.nping;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class NpingTCPDelayMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    double maxRTT;
    double minRTT;
    double avgRTT;
    int tcpConnectionAttempts;
    int tcpSuccessfulConnections;
    int tcpFailedConnectionsAbsolute;
    double tcpFailedConnectionsRelative;

    public NpingTCPDelayMeasurement(double maxRTT, double minRTT, double avgRTT, int tcpConnectionAttempts, int tcpSuccessfulConnections, int tcpFailedConnectionsAbsolute, double tcpFailedConnectionsRelative) {
        this.maxRTT = maxRTT;
        this.minRTT = minRTT;
        this.avgRTT = avgRTT;
        this.tcpConnectionAttempts = tcpConnectionAttempts;
        this.tcpSuccessfulConnections = tcpSuccessfulConnections;
        this.tcpFailedConnectionsAbsolute = tcpFailedConnectionsAbsolute;
        this.tcpFailedConnectionsRelative = tcpFailedConnectionsRelative;
    }
}
