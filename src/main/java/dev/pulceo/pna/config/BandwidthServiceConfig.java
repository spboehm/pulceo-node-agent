package dev.pulceo.pna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.PollableChannel;

@Configuration
public class BandwidthServiceConfig {

    // TODO: Choose another queue type
    @Bean
    public PollableChannel bandwidthServiceMessageChannel() {
        return new QueueChannel();
    }

}
