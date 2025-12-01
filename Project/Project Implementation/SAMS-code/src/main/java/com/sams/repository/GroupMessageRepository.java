package com.sams.repository;

import com.sams.entity.GroupMessage;
import com.sams.entity.StudyGroup;
import com.sams.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * repository for group message data access
 * handles study group chat messages
 * needed this becuase we support group messaging features
 */
@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage, Long> {

    // find all messages in a group (non-deleted only), ordered by most recent, with pagination - used in chat view
    Page<GroupMessage> findByStudyGroupIdAndDeletedFalseOrderBySentAtDesc(Long groupId, Pageable pageable);

    // find all messages in a group (non-deleted only), ordered by oldest first - for loading history
    List<GroupMessage> findByStudyGroupIdAndDeletedFalseOrderBySentAtAsc(Long groupId);

    // count total messages in a group (excluding deleted)
    long countByStudyGroupIdAndDeletedFalse(Long groupId);

    // find all messages sent by a specific user in a group
    List<GroupMessage> findByStudyGroupIdAndSenderIdAndDeletedFalseOrderBySentAtDesc(Long groupId, Long senderId);

    // custom query for file-type messages in a group - for file browser feature
    // needed this becuase JPA doesnt support IN clause with enums easily
    @Query("SELECT m FROM GroupMessage m WHERE m.studyGroup.id = :groupId AND m.deleted = false AND m.messageType IN ('FILE', 'IMAGE') ORDER BY m.sentAt DESC")
    List<GroupMessage> findFileMessagesByGroup(@Param("groupId") Long groupId);

    // custom query for recent messages in a group (last N messages) - useful for quick preview
    @Query("SELECT m FROM GroupMessage m WHERE m.studyGroup.id = :groupId AND m.deleted = false ORDER BY m.sentAt DESC")
    List<GroupMessage> findRecentMessages(@Param("groupId") Long groupId, Pageable pageable);

    // check if a user has sent any messages in a group - for validation
    boolean existsByStudyGroupIdAndSenderIdAndDeletedFalse(Long groupId, Long senderId);

    // find all messages by sender (across all groups) - for user activity tracking
    List<GroupMessage> findBySenderIdAndDeletedFalseOrderBySentAtDesc(Long senderId);

    // custom query for searching messages in a group by content (case insensitive)
    // needed this becuase we need flexible text search functionality
    @Query("SELECT m FROM GroupMessage m WHERE m.studyGroup.id = :groupId AND m.deleted = false AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY m.sentAt DESC")
    List<GroupMessage> searchMessagesByContent(@Param("groupId") Long groupId, @Param("searchTerm") String searchTerm);
}
