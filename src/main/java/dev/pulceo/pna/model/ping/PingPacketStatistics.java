package dev.pulceo.pna.model.ping;

import lombok.Data;

@Data
public class PingPacketStatistics {

        private final int packetsTransmitted;
        private final int packetsReceived;
        private final float packetLoss;
        private final int time;

        public PingPacketStatistics(int packetsTransmitted, int packetsReceived, float packetLoss, int time) {
            this.packetsTransmitted = packetsTransmitted;
            this.packetsReceived = packetsReceived;
            this.packetLoss = packetLoss;
            this.time = time;
        }
    }
