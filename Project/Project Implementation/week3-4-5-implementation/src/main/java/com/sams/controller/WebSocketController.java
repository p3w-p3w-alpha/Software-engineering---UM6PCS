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
     * Routes to specific group topic based on groupId
     * @param message The message content with groupId
     */
    @MessageMapping("/group/send")
    public void sendGroupMessage(Map<String, Object> message) {
        Object groupId = message.get("groupId");
        if (groupId != null) {
            message.put("timestamp", LocalDateTime.now().toString());
            // Send to the specific group topic
            messagingTemplate.convertAndSend("/topic/group/" + groupId, message);
        }
    }

    /**
     * Handle private messages sent to specific users
     * @param message The message content with recipientId
     */
    @MessageMapping("/private/send")
    public void handlePrivateMessage(Map<String, Object> message) {
        Object recipientId = message.get("recipientId");
        if (recipientId != null) {
            message.put("timestamp", LocalDateTime.now().toString());
            // Send to the recipient's queue
            messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/messages",
                message
            );
        }
    }

    /**
     * Handle typing indicators
     * @param typingData The typing indicator data with senderId, recipientId, isTyping
     */
    @MessageMapping("/typing")
    public void handleTypingIndicator(Map<String, Object> typingData) {
        Object recipientId = typingData.get("recipientId");
        if (recipientId != null) {
            messagingTemplate.convertAndSendToUser(
                recipientId.toString(),
                "/queue/typing",
                typingData
            );
        }
    }

    /**
     * Send private message to a specific user (programmatic)
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
