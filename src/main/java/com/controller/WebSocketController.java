package com.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifySubscribersToTopic(String message, String topic) {
        messagingTemplate.convertAndSend("/topic", message);
    }

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public String greeting(String message) throws Exception {
        Thread.sleep(1000); // simulated delay
        return message;
    }
}
