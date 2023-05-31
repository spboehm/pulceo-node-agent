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
public class NpingUDPDelayMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
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
