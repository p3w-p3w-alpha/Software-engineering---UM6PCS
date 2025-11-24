package com.sams.service;

import com.sams.entity.PrivateMessage;
import com.sams.entity.User;
import com.sams.exception.ResourceNotFoundException;
import com.sams.repository.PrivateMessageRepository;
import com.sams.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// service for managing private messages between connected users
@Service
public class PrivateMessageService {

    private final PrivateMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConnectionService connectionService;
    private final NotificationService notificationService;

    public PrivateMessageService(PrivateMessageRepository messageRepository,
                                UserRepository userRepository,
                                ConnectionService connectionService,
                                NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.connectionService = connectionService;
        this.notificationService = notificationService;
    }

    // ========== MESSAGING OPERATIONS ==========

    /**
     * Send a private message
     * Users must be connected to send messages
     */
    @Transactional
    public PrivateMessage sendMessage(Long senderId, Long receiverId, String content) {
        // validate users exist
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found with id: " + senderId));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found with id: " + receiverId));

        // validate content
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (content.length() > 2000) {
            throw new IllegalArgumentException("Message exceeds maximum length of 2000 characters");
        }

        // chekc if users are connected
        if (!connectionService.areUsersConnected(senderId, receiverId)) {
            throw new IllegalStateException("Can only send messages to connected users");
        }

        // chekc if either user has blocked the other
        if (connectionService.hasUserBlocked(receiverId, senderId)) {
            throw new IllegalStateException("Cannot send message to user who has blocked you");
        }

        if (connectionService.hasUserBlocked(senderId, receiverId)) {
            throw new IllegalStateException("Cannot send message to blocked user");
        }

        // create and save message
        PrivateMessage message = new PrivateMessage(sender, receiver, content);
        PrivateMessage saved = messageRepository.save(message);

        // notify receiver of new message
        notificationService.notifyPrivateMessageReceived(receiver, sender);

        return saved;
    }

    /**
     * Mark a message as read
     */
    @Transactional
    public PrivateMessage markMessageAsRead(Long messageId, Long userId) {
        PrivateMessage message = getMessageById(messageId);

        // only receiver can mark message as read
        if (!message.getReceiver().getId().equals(userId)) {
            throw new IllegalStateException("Only the receiver can mark message as read");
        }

        message.markAsRead();
        return messageRepository.save(message);
    }

    /**
     * Mark all messages in a conversation as read
     */
    @Transactional
    public void markConversationAsRead(Long user1Id, Long user2Id) {
        List<PrivateMessage> messages = messageRepository.findConversation(user1Id, user2Id);

        for (PrivateMessage message : messages) {
            // only mark messages where user1 is the receiver
            if (message.getReceiver().getId().equals(user1Id) && message.isUnread()) {
                message.markAsRead();
                messageRepository.save(message);
            }
        }
    }

    /**
     * Delete a message
     * Both sender and receiver can delete messages
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        PrivateMessage message = getMessageById(messageId);

        // validate user is involved in this message
        if (!message.involvesUser(userId)) {
            throw new IllegalStateException("You don't have permission to delete this message");
        }

        User deletedBy = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        message.delete(deletedBy);
        messageRepository.save(message);
    }

    // ========== QUERY OPERATIONS ==========

    /**
     * Get message by ID
     */
    public PrivateMessage getMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + id));
    }

    /**
     * Get conversation between two users
     */
    public List<PrivateMessage> getConversation(Long user1Id, Long user2Id) {
        return messageRepository.findConversation(user1Id, user2Id);
    }

    /**
     * Get conversation with pagination
     */
    public Page<PrivateMessage> getConversationPaginated(Long user1Id, Long user2Id, Pageable pageable) {
        return messageRepository.findConversationPaginated(user1Id, user2Id, pageable);
    }

    /**
     * Get recent messages in a conversation
     */
    public List<PrivateMessage> getRecentMessages(Long user1Id, Long user2Id, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return messageRepository.findRecentMessages(user1Id, user2Id, pageable);
    }

    /**
     * Get all unread messages for a user
     */
    public List<PrivateMessage> getUnreadMessages(Long userId) {
        return messageRepository.findUnreadMessages(userId);
    }

    /**
     * Get count of unread messages for a user
     */
    public long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessages(userId);
    }

    /**
     * Get count of unread messages from a specific user
     */
    public long getUnreadMessageCountFromUser(Long receiverId, Long senderId) {
        return messageRepository.countUnreadMessagesFromUser(receiverId, senderId);
    }

    /**
     * Get all messages sent by a user
     */
    public List<PrivateMessage> getMessagesSentByUser(Long userId) {
        return messageRepository.findMessagesSentByUser(userId);
    }

    /**
     * Get all messages received by a user
     */
    public List<PrivateMessage> getMessagesReceivedByUser(Long userId) {
        return messageRepository.findMessagesReceivedByUser(userId);
    }

    /**
     * Get latest message in each conversation for a user
     * Useful for displaying conversation list
     */
    public List<PrivateMessage> getLatestMessagesForUser(Long userId) {
        return messageRepository.findLatestMessagesForUser(userId);
    }

    /**
     * Search messages in a conversation
     */
    public List<PrivateMessage> searchConversation(Long user1Id, Long user2Id, String searchTerm) {
        return messageRepository.searchConversation(user1Id, user2Id, searchTerm);
    }

    /**
     * Chekc if user has permission to view a message
     */
    public boolean canUserViewMessage(Long messageId, Long userId) {
        try {
            PrivateMessage message = getMessageById(messageId);
            return message.involvesUser(userId);
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
