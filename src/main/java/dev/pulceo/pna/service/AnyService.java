package dev.pulceo.pna.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

@Service
public class AnyService {

    @Autowired
    private MessageChannel mqttOutboundChannel;

    public void test() {
        Message<String> message = new GenericMessage<String>("adsdsahdsajh");
        mqttOutboundChannel.send(message);
    }


}
