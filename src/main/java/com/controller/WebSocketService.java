package com.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class WebSocketService {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifySubscribersToTopic(String message, String topic) {
        messagingTemplate.convertAndSend("/topic/" + topic, message);
    }
}
