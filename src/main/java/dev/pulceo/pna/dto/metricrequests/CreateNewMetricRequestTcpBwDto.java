package dev.pulceo.pna.dto.metricrequests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateNewMetricRequestTcpBwDto {
        private String type;
        private String recurrence;
        private boolean enabled;
        private long port;
        @Builder.Default
        private int bitrate = 50;
        @Builder.Default
        private int time = 5;
        @Builder.Default
        private int initialDelay = 0;
}
