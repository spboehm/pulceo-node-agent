package dev.pulceo.pna.model.nping;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NpingTCPDelayMeasurement extends Resource {

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
