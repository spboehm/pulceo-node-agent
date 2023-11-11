package dev.pulceo.pna.model.nping;

import dev.pulceo.pna.model.Resource;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class NpingUDPDelayMeasurement extends Resource {

    double maxRTT;
    double minRTT;
    double avgRTT;
    int udpPacketsSent;
    int udpReceivedPackets;
    int udpLostPacketsAbsolute;
    double udpLostPacketsRelative;

    public NpingUDPDelayMeasurement(double maxRTT, double minRTT, double avgRTT, int udpPacketsSent, int udpReceivedPackets, int udpLostAbsolute, double udpLostPacketsRelative) {
        this.maxRTT = maxRTT;
        this.minRTT = minRTT;
        this.avgRTT = avgRTT;
        this.udpPacketsSent = udpPacketsSent;
        this.udpReceivedPackets = udpReceivedPackets;
        this.udpLostPacketsAbsolute = udpLostAbsolute;
        this.udpLostPacketsRelative = udpLostPacketsRelative;
    }
}
