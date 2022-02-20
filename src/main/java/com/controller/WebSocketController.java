package com.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

//    @SendTo("/topic/greetings")
public void notifySubscribersToTopic(String message, String topic) {
    messagingTemplate.convertAndSend("/topic/greetings", message);
}

    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return message;
    }
}
