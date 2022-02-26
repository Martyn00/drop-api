package com.controller;

import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    public void notifySubscribersToFileChanges(String message, String uuid) {
        messagingTemplate.convertAndSend("/topic/" + uuid, message);
    }

    public void notifySubscribersToFolderChanges(String message, String uuid) {
        messagingTemplate.convertAndSend("/topic/shared-folders/" + uuid, message);
    }
}

