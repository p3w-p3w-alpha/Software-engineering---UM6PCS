package com.sams.repository;

import com.sams.entity.NotificationPreference;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {

    // find preference settings for a user
    Optional<NotificationPreference> findByUser(User user);

    // find preference settings by user id
    Optional<NotificationPreference> findByUserId(Long userId);

    // check if preferences exist for a user
    boolean existsByUserId(Long userId);
}
