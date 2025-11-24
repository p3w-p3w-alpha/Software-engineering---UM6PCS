package com.sams.repository;

import com.sams.entity.Notification;
import com.sams.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // find all notifications for a user (paginated)
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    // find all notifications for a user by user id
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    // find all notifications for a user by user id (paginated)
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // find unread notifications for a user
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    // find unread notifications for a user by user id
    List<Notification> findByUserIdAndReadFalseOrderByCreatedAtDesc(Long userId);

    // count unread notifications for a user
    long countByUserAndReadFalse(User user);

    // count unread notifications by user id
    long countByUserIdAndReadFalse(Long userId);

    // find notifications by type for a user
    List<Notification> findByUserAndTypeOrderByCreatedAtDesc(User user, String type);

    // find read notifications for a user
    List<Notification> findByUserAndReadTrueOrderByCreatedAtDesc(User user);

    // delete all read notifications for a user
    void deleteByUserAndReadTrue(User user);
}
