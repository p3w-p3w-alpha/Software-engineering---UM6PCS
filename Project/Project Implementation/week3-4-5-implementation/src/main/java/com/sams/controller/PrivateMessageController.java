package com.sams.controller;

import com.sams.dto.PrivateMessageRequest;
import com.sams.dto.PrivateMessageResponse;
import com.sams.entity.PrivateMessage;
import com.sams.service.PrivateMessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// REST controller for private messaging operations
@RestController
@RequestMapping("/api/messages")
public class PrivateMessageController {

    private final PrivateMessageService messageService;

    public PrivateMessageController(PrivateMessageService messageService) {
        this.messageService = messageService;
    }

    // ========== MESSAGING OPERATIONS ==========

    /**
     * Send a private message
     * POST /api/messages/send?senderId=1&receiverId=2
     */
    @PostMapping("/send")
    public ResponseEntity<PrivateMessageResponse> sendMessage(
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @Valid @RequestBody PrivateMessageRequest request) {

        PrivateMessage message = messageService.sendMessage(senderId, receiverId, request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(message));
    }

    /**
     * Mark a message as read
     * POST /api/messages/{id}/read?userId=2
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<PrivateMessageResponse> markMessageAsRead(
            @PathVariable Long id,
            @RequestParam Long userId) {

        PrivateMessage message = messageService.markMessageAsRead(id, userId);
        return ResponseEntity.ok(convertToResponse(message));
    }

    /**
     * Mark all messages in a conversation as read
     * POST /api/messages/conversation/read?user1Id=1&user2Id=2
     */
    @PostMapping("/conversation/read")
    public ResponseEntity<Map<String, String>> markConversationAsRead(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        messageService.markConversationAsRead(user1Id, user2Id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Conversation marked as read");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a message
     * DELETE /api/messages/{id}?userId=1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable Long id,
            @RequestParam Long userId) {

        messageService.deleteMessage(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Message deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ========== QUERY OPERATIONS ==========

    /**
     * Get message by ID
     * GET /api/messages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrivateMessageResponse> getMessageById(@PathVariable Long id) {
        PrivateMessage message = messageService.getMessageById(id);
        return ResponseEntity.ok(convertToResponse(message));
    }

    /**
     * Get conversation between two users
     * GET /api/messages/conversation?user1Id=1&user2Id=2
     */
    @GetMapping("/conversation")
    public ResponseEntity<List<PrivateMessageResponse>> getConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id) {

        List<PrivateMessage> messages = messageService.getConversation(user1Id, user2Id);
        List<PrivateMessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get conversation with pagination
     * GET /api/messages/conversation/paginated?user1Id=1&user2Id=2&page=0&size=20
     */
    @GetMapping("/conversation/paginated")
    public ResponseEntity<Page<PrivateMessageResponse>> getConversationPaginated(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<PrivateMessage> messages = messageService.getConversationPaginated(user1Id, user2Id, pageable);
        Page<PrivateMessageResponse> responses = messages.map(this::convertToResponse);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get recent messages in a conversation
     * GET /api/messages/conversation/recent?user1Id=1&user2Id=2&limit=50
     */
    @GetMapping("/conversation/recent")
    public ResponseEntity<List<PrivateMessageResponse>> getRecentMessages(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id,
            @RequestParam(defaultValue = "50") int limit) {

        List<PrivateMessage> messages = messageService.getRecentMessages(user1Id, user2Id, limit);
        List<PrivateMessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get all unread messages for a user
     * GET /api/messages/user/{userId}/unread
     */
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<PrivateMessageResponse>> getUnreadMessages(@PathVariable Long userId) {
        List<PrivateMessage> messages = messageService.getUnreadMessages(userId);
        List<PrivateMessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get count of unread messages for a user
     * GET /api/messages/user/{userId}/unread-count
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCount(@PathVariable Long userId) {
        long count = messageService.getUnreadMessageCount(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    /**
     * Get count of unread messages from a specific user
     * GET /api/messages/unread-count?receiverId=1&senderId=2
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadMessageCountFromUser(
            @RequestParam Long receiverId,
            @RequestParam Long senderId) {

        long count = messageService.getUnreadMessageCountFromUser(receiverId, senderId);

        Map<String, Long> response = new HashMap<>();
        response.put("unreadCount", count);

        return ResponseEntity.ok(response);
    }

    /**
     * Get latest message in each conversation for a user
     * GET /api/messages/user/{userId}/conversations
     */
    @GetMapping("/user/{userId}/conversations")
    public ResponseEntity<List<PrivateMessageResponse>> getLatestMessagesForUser(@PathVariable Long userId) {
        List<PrivateMessage> messages = messageService.getLatestMessagesForUser(userId);
        List<PrivateMessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Search messages in a conversation
     * GET /api/messages/conversation/search?user1Id=1&user2Id=2&query=homework
     */
    @GetMapping("/conversation/search")
    public ResponseEntity<List<PrivateMessageResponse>> searchConversation(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id,
            @RequestParam String query) {

        List<PrivateMessage> messages = messageService.searchConversation(user1Id, user2Id, query);
        List<PrivateMessageResponse> responses = messages.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ========== HELPER METHODS FOR DTO CONVERSION ==========

    // convert PrivateMessage entity to PrivateMessageResponse
    private PrivateMessageResponse convertToResponse(PrivateMessage message) {
        PrivateMessageResponse response = new PrivateMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setSentAt(message.getSentAt());
        response.setRead(message.getRead());
        response.setReadAt(message.getReadAt());
        response.setDeleted(message.getDeleted());

        // set sender info
        if (message.getSender() != null) {
            response.setSenderId(message.getSender().getId());
            response.setSenderName(message.getSender().getFirstName() + " " +
                    message.getSender().getLastName());
        }

        // set receiver info
        if (message.getReceiver() != null) {
            response.setReceiverId(message.getReceiver().getId());
            response.setReceiverName(message.getReceiver().getFirstName() + " " +
                    message.getReceiver().getLastName());
        }

        return response;
    }
}
