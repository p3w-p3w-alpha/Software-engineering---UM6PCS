package com.sams.dto.dashboard;

import java.time.LocalDateTime;

/**
 * DTO for recent activity logs
 */
public class RecentActivity {

    private String activityType; // USER_CREATED, ENROLLMENT_ADDED, GRADE_ASSIGNED, etc.
    private String description;
    private String performedBy;
    private LocalDateTime timestamp;
    private String icon; // Optional: for frontend display

    // Constructors
    public RecentActivity() {
    }

    public RecentActivity(String activityType, String description, String performedBy, LocalDateTime timestamp) {
        this.activityType = activityType;
        this.description = description;
        this.performedBy = performedBy;
        this.timestamp = timestamp;
    }

    // Getters and Setters
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

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
