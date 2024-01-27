package dev.pulceo.pna.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.ip.dsl.Udp;

@Configuration
public class UdpConfig {

    @Value("${pna.local.address}")
    private String localAddress;

    @Value("${pna.delay.udp.port:4001}")
    private int npingDelayUDPPort;

    // TODO: Replace with traditional Java Config
    @Bean
    public IntegrationFlow udpServerForNping() {
        return IntegrationFlow.from(Udp.inboundAdapter(npingDelayUDPPort).localAddress(localAddress).id("udpIn"))
                .handle(Udp.outboundAdapter("headers['ip_packetAddress']")
                        .socketExpression("@udpIn.socket"))
                .get();
    }

}
