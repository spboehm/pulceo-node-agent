package dev.pulceo.pna.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.router.HeaderValueRouter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MQTTConfig {

    @Value("${pna.id}")
    private String deviceId;

    @Value("${pna.mqtt.client.id}")
    private String mqttClientId;

    /* Inbound */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", mqttClientId,
                        "topic1");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.connectComplete(true);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "channelA")
    public MessageHandler handler() {
        return message -> System.out.println(message.getHeaders());
    }

    @Bean
    public IntegrationFlow routerFlow1() {
        return IntegrationFlow.from("mqttInputChannel")
                .route(router())
                .get();
    }

    @Bean
    public HeaderValueRouter router() {
        HeaderValueRouter router = new HeaderValueRouter("mqtt_receivedTopic");
        router.setChannelMapping("topic1", "channelA");
        router.setChannelMapping("topic2", "channelB");
        return router;
    }

    @Bean
    public MessageChannel channelA() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel channelB() {
        return new DirectChannel();
    }

    /* Outbound */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { "tcp://localhost:1883"});
        options.setAutomaticReconnect(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(deviceId, mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic(deviceId + "/");
        messageHandler.setConverter(new DefaultPahoMessageConverter());
        return messageHandler;
    }

}
