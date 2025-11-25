package com.sams.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * WebSocket controller for handling real-time messages
 * Supports group messages and private messages
 */
@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle messages sent to study groups
     * @param message The message content
     * @return Broadcast message to all group members
     */
    @MessageMapping("/group/send")
    @SendTo("/topic/group")
    public Map<String, Object> sendGroupMessage(Map<String, Object> message) {
        message.put("timestamp", LocalDateTime.now().toString());
        return message;
    }

    /**
     * Send private message to a specific user
     */
    public void sendPrivateMessage(String username, Map<String, Object> message) {
        message.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSendToUser(username, "/queue/messages", message);
    }

    /**
     * Send notification to a specific user
     */
    public void sendNotification(String username, Map<String, Object> notification) {
        notification.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSendToUser(username, "/queue/notifications", notification);
    }

    /**
     * Broadcast notification to all users
     */
    public void broadcastNotification(Map<String, Object> notification) {
        notification.put("timestamp", LocalDateTime.now().toString());
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
}
