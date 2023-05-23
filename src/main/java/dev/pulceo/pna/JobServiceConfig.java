package dev.pulceo.pna;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.PollableChannel;

@Configuration
public class JobServiceConfig {

    @Bean
    public PollableChannel jobServiceChannel() {
        return new QueueChannel();
    }

}
