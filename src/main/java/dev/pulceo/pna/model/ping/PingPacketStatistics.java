package dev.pulceo.pna.model.ping;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PingPacketStatistics {

        private int packetsTransmitted;
        private int packetsReceived;
        private float packetLoss;
        private int time;

        public PingPacketStatistics(int packetsTransmitted, int packetsReceived, float packetLoss, int time) {
            this.packetsTransmitted = packetsTransmitted;
            this.packetsReceived = packetsReceived;
            this.packetLoss = packetLoss;
            this.time = time;
        }
    }
