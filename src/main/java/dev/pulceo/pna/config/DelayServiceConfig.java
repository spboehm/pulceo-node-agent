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
    public PublishSubscribeChannel npingTcpPubSubChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public PublishSubscribeChannel npingUdpPubSubChannel() {
        return new PublishSubscribeChannel();
    }

    @Autowired
    MessageChannel mqttOutboundChannel;

    @Bean
    public IntegrationFlow routerFlow3() {
        return IntegrationFlow.from("npingTcpPubSubChannel")
                .transform(Transformers.toJson())
                .route(router3())
                .get();
    }

    @Bean
    public IntegrationFlow routerFlow3_1() {
        return IntegrationFlow.from("npingUdpPubSubChannel")
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
