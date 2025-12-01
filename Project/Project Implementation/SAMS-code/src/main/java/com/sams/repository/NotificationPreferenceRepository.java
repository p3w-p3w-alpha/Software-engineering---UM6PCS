package com.sams.repository;

import com.sams.entity.NotificationPreference;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * repository for notification preferences
 * stores user settings for email/sms notifications
 */
@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    // find preference settings for a user - used in settings page
    Optional<NotificationPreference> findByUser(User user);

    // find preference settings by user id - alternative method for performace
    Optional<NotificationPreference> findByUserId(Long userId);

    // check if preferences exist for a user - needed becuase we create defaults on first login
    boolean existsByUserId(Long userId);
}
