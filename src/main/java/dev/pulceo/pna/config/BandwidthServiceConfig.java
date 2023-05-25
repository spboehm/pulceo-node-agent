package dev.pulceo.pna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;

@Configuration
public class BandwidthServiceConfig {

    @Bean
    public PublishSubscribeChannel bandwidthServiceMessageChannel() {
        return new PublishSubscribeChannel();
    }

}
