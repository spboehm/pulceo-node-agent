package dev.pulceo.pna.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;
import org.springframework.integration.router.PayloadTypeRouter;
import org.springframework.messaging.MessageChannel;

@Configuration
public class DelayServiceConfig {

    @Bean
    public PublishSubscribeChannel delayServiceMessageChannel() {
        return new PublishSubscribeChannel();
    }

    @Autowired
    MessageChannel mqttOutboundChannel;

    @Bean
    public IntegrationFlow routerFlow2() {
        return IntegrationFlow.from("delayServiceMessageChannel")
                .transform(Transformers.toJson())
                .route(router3())
                .get();
    }

    @Bean
    public PayloadTypeRouter router3() {
        PayloadTypeRouter router = new PayloadTypeRouter();
        router.setDefaultOutputChannel(mqttOutboundChannel);
        return router;
    }
}
