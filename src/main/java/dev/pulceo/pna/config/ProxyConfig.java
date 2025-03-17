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
public class ProxyConfig {

    @Bean
    public PublishSubscribeChannel proxyMessageChannel() {
        return new PublishSubscribeChannel();
    }

    @Autowired
    MessageChannel mqttOutboundChannel;

    @Bean
    public IntegrationFlow routerFlow6() {
        return IntegrationFlow.from("proxyMessageChannel")
                .transform(Transformers.toJson())
                .route(router6())
                .get();
    }

    @Bean
    public PayloadTypeRouter router6() {
        PayloadTypeRouter router = new PayloadTypeRouter();
        router.setDefaultOutputChannel(mqttOutboundChannel);
        return router;
    }
}
