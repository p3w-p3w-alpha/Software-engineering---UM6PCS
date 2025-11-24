package com.sams.repository;

import com.sams.entity.PrivateMessage;
import com.sams.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    // find all messages in a conversation between two users (non-deleted), ordered by time
    @Query("SELECT m FROM PrivateMessage m WHERE m.deleted = false AND " +
           "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) " +
           "ORDER BY m.sentAt ASC")
    List<PrivateMessage> findConversation(@Param("user1Id") Long user1Id,
                                         @Param("user2Id") Long user2Id);

    // find all messages in a conversation with pagination
    @Query("SELECT m FROM PrivateMessage m WHERE m.deleted = false AND " +
           "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) " +
           "ORDER BY m.sentAt DESC")
    Page<PrivateMessage> findConversationPaginated(@Param("user1Id") Long user1Id,
                                                   @Param("user2Id") Long user2Id,
                                                   Pageable pageable);

    // find all unread messages for a user
    @Query("SELECT m FROM PrivateMessage m WHERE " +
           "m.deleted = false AND m.read = false AND m.receiver.id = :userId " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> findUnreadMessages(@Param("userId") Long userId);

    // count unread messages for a user
    @Query("SELECT COUNT(m) FROM PrivateMessage m WHERE " +
           "m.deleted = false AND m.read = false AND m.receiver.id = :userId")
    long countUnreadMessages(@Param("userId") Long userId);

    // count unread messages from a specific user
    @Query("SELECT COUNT(m) FROM PrivateMessage m WHERE " +
           "m.deleted = false AND m.read = false AND " +
           "m.receiver.id = :receiverId AND m.sender.id = :senderId")
    long countUnreadMessagesFromUser(@Param("receiverId") Long receiverId,
                                     @Param("senderId") Long senderId);

    // find all messages sent by a user
    @Query("SELECT m FROM PrivateMessage m WHERE " +
           "m.deleted = false AND m.sender.id = :userId " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> findMessagesSentByUser(@Param("userId") Long userId);

    // find all messages received by a user
    @Query("SELECT m FROM PrivateMessage m WHERE " +
           "m.deleted = false AND m.receiver.id = :userId " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> findMessagesReceivedByUser(@Param("userId") Long userId);

    // get latest message in each conversation for a user
    @Query("SELECT m FROM PrivateMessage m WHERE m.id IN (" +
           "SELECT MAX(pm.id) FROM PrivateMessage pm WHERE pm.deleted = false AND " +
           "(pm.sender.id = :userId OR pm.receiver.id = :userId) " +
           "GROUP BY CASE " +
           "WHEN pm.sender.id = :userId THEN pm.receiver.id " +
           "ELSE pm.sender.id END) " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> findLatestMessagesForUser(@Param("userId") Long userId);

    // search messages in a conversation by content
    @Query("SELECT m FROM PrivateMessage m WHERE m.deleted = false AND " +
           "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) AND " +
           "LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> searchConversation(@Param("user1Id") Long user1Id,
                                           @Param("user2Id") Long user2Id,
                                           @Param("searchTerm") String searchTerm);

    // get recent messages (last N messages) in a conversation
    @Query("SELECT m FROM PrivateMessage m WHERE m.deleted = false AND " +
           "((m.sender.id = :user1Id AND m.receiver.id = :user2Id) OR " +
           "(m.sender.id = :user2Id AND m.receiver.id = :user1Id)) " +
           "ORDER BY m.sentAt DESC")
    List<PrivateMessage> findRecentMessages(@Param("user1Id") Long user1Id,
                                           @Param("user2Id") Long user2Id,
                                           Pageable pageable);
}
