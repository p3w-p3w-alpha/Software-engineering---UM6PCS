package com.sams.repository;

import com.sams.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    // Find recent activities (ordered by creation time descending)
    List<ActivityLog> findAllByOrderByCreatedAtDesc();

    // Find activities with pagination
    Page<ActivityLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    // Find activities by type
    List<ActivityLog> findByActivityTypeOrderByCreatedAtDesc(String activityType);

    // Find activities by entity type
    List<ActivityLog> findByEntityTypeOrderByCreatedAtDesc(String entityType);

    // Find activities by user
    List<ActivityLog> findByPerformedByIdOrderByCreatedAtDesc(Long userId);

    // Find activities in date range
    @Query("SELECT a FROM ActivityLog a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate ORDER BY a.createdAt DESC")
    List<ActivityLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Find recent activities with limit
    @Query("SELECT a FROM ActivityLog a ORDER BY a.createdAt DESC")
    List<ActivityLog> findRecentActivities(Pageable pageable);
}
