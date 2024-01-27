package dev.pulceo.pna.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNetServerConnectionFactory;
import org.springframework.messaging.MessageChannel;

@Configuration
public class TcpConfig {

    @Value("${pna.local.address}")
    private String localAddress;

    @Value("${pna.delay.tcp.port:4002}")
    private int npingDelayTCPPort;

    @Bean
    public AbstractServerConnectionFactory serverCF() {
        TcpNetServerConnectionFactory serverCF = new TcpNetServerConnectionFactory(this.npingDelayTCPPort);
        serverCF.setLocalAddress(localAddress);
        return serverCF;
    }

    @Bean
    public TcpInboundGateway tcpInGate(AbstractServerConnectionFactory connectionFactory)  {
        TcpInboundGateway inGate = new TcpInboundGateway();
        inGate.setConnectionFactory(connectionFactory);
        inGate.setRequestChannel(fromTcp());
        return inGate;
    }

    @Bean
    public MessageChannel fromTcp() {
        return new DirectChannel();
    }

}
