package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for tracking system activity/audit logs
 */
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Type of activity: USER_CREATED, USER_UPDATED, ENROLLMENT_CREATED, GRADE_ASSIGNED,
    // PAYMENT_SUBMITTED, PAYMENT_APPROVED, COURSE_CREATED, etc.
    @Column(name = "activity_type", nullable = false)
    private String activityType;

    // Human-readable description of the activity
    @Column(name = "description", length = 500)
    private String description;

    // Entity type affected: USER, COURSE, ENROLLMENT, GRADE, PAYMENT, etc.
    @Column(name = "entity_type")
    private String entityType;

    // ID of the entity affected
    @Column(name = "entity_id")
    private Long entityId;

    // User who performed the action (null for system actions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    // Username stored for quick access (avoids join for display)
    @Column(name = "performed_by_username")
    private String performedByUsername;

    // IP address of the request (if applicable)
    @Column(name = "ip_address")
    private String ipAddress;

    // Additional metadata in JSON format
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ActivityLog() {
    }

    public ActivityLog(String activityType, String description, String entityType, Long entityId) {
        this.activityType = activityType;
        this.description = description;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    // Static factory methods for common activities
    public static ActivityLog userCreated(User user, User performedBy) {
        ActivityLog log = new ActivityLog();
        log.setActivityType("USER_CREATED");
        log.setDescription("New user created: " + user.getUsername());
        log.setEntityType("USER");
        log.setEntityId(user.getId());
        log.setPerformedBy(performedBy);
        log.setPerformedByUsername(performedBy != null ? performedBy.getUsername() : "System");
        return log;
    }

    public static ActivityLog enrollmentCreated(Long enrollmentId, String studentName, String courseName, User performedBy) {
        ActivityLog log = new ActivityLog();
        log.setActivityType("ENROLLMENT_CREATED");
        log.setDescription("Student " + studentName + " enrolled in " + courseName);
        log.setEntityType("ENROLLMENT");
        log.setEntityId(enrollmentId);
        log.setPerformedBy(performedBy);
        log.setPerformedByUsername(performedBy != null ? performedBy.getUsername() : "System");
        return log;
    }

    public static ActivityLog gradeAssigned(Long gradeId, String studentName, String courseName, String grade, User performedBy) {
        ActivityLog log = new ActivityLog();
        log.setActivityType("GRADE_ASSIGNED");
        log.setDescription("Grade " + grade + " assigned to " + studentName + " for " + courseName);
        log.setEntityType("GRADE");
        log.setEntityId(gradeId);
        log.setPerformedBy(performedBy);
        log.setPerformedByUsername(performedBy != null ? performedBy.getUsername() : "System");
        return log;
    }

    public static ActivityLog paymentApproved(Long paymentId, String studentName, String amount, User performedBy) {
        ActivityLog log = new ActivityLog();
        log.setActivityType("PAYMENT_APPROVED");
        log.setDescription("Payment of $" + amount + " approved for " + studentName);
        log.setEntityType("PAYMENT");
        log.setEntityId(paymentId);
        log.setPerformedBy(performedBy);
        log.setPerformedByUsername(performedBy != null ? performedBy.getUsername() : "System");
        return log;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public String getPerformedByUsername() {
        return performedByUsername;
    }

    public void setPerformedByUsername(String performedByUsername) {
        this.performedByUsername = performedByUsername;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
