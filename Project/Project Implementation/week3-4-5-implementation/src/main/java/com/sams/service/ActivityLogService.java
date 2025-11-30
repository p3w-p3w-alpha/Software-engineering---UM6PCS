package com.sams.service;

import com.sams.entity.ActivityLog;
import com.sams.entity.User;
import com.sams.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for managing activity logs
 */
@Service
@Transactional
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    /**
     * Log an activity
     */
    public ActivityLog log(String activityType, String description, String entityType, Long entityId, User performedBy) {
        ActivityLog log = new ActivityLog();
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setPerformedBy(performedBy);
        log.setPerformedByUsername(performedBy != null ? performedBy.getUsername() : "System");
        return activityLogRepository.save(log);
    }

    /**
     * Log user creation
     */
    public ActivityLog logUserCreated(User user, User performedBy) {
        return activityLogRepository.save(ActivityLog.userCreated(user, performedBy));
    }

    /**
     * Log enrollment creation
     */
    public ActivityLog logEnrollmentCreated(Long enrollmentId, String studentName, String courseName, User performedBy) {
        return activityLogRepository.save(ActivityLog.enrollmentCreated(enrollmentId, studentName, courseName, performedBy));
    }

    /**
     * Log grade assignment
     */
    public ActivityLog logGradeAssigned(Long gradeId, String studentName, String courseName, String grade, User performedBy) {
        return activityLogRepository.save(ActivityLog.gradeAssigned(gradeId, studentName, courseName, grade, performedBy));
    }

    /**
     * Log payment approval
     */
    public ActivityLog logPaymentApproved(Long paymentId, String studentName, String amount, User performedBy) {
        return activityLogRepository.save(ActivityLog.paymentApproved(paymentId, studentName, amount, performedBy));
    }

    /**
     * Get recent activities
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getRecentActivities(int limit) {
        return activityLogRepository.findRecentActivities(PageRequest.of(0, limit));
    }

    /**
     * Get all activities
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getAllActivities() {
        return activityLogRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Get activities by type
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByType(String activityType) {
        return activityLogRepository.findByActivityTypeOrderByCreatedAtDesc(activityType);
    }

    /**
     * Get activities by user
     */
    @Transactional(readOnly = true)
    public List<ActivityLog> getActivitiesByUser(Long userId) {
        return activityLogRepository.findByPerformedByIdOrderByCreatedAtDesc(userId);
    }
}
