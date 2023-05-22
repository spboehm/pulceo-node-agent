package dev.pulceo.pna;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.zeromq.ZeroMqProxy;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.zeromq.ZContext;

@Configuration
@EnableAsync
@EnableScheduling
public class Config {

    @Bean
    ZeroMqProxy zeroMqProxy() {
        ZeroMqProxy proxy = new ZeroMqProxy(new ZContext(), ZeroMqProxy.Type.SUB_PUB);
        proxy.setExposeCaptureSocket(true);
        proxy.setFrontendPort(6001);
        proxy.setBackendPort(6002);
        return proxy;
    }


}
