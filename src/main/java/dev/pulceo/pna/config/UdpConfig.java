package dev.pulceo.pna.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.dsl.Udp;

@Configuration
public class UdpConfig {

    // TODO: Replace with traditional Java Config
    @Bean
    public IntegrationFlow udpServerForNping() {
        return IntegrationFlow.from(Udp.inboundAdapter(4001).id("udpIn"))
                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket"))
                .get();
    }

}
