package com.sams.dto;

/**
 * DTO for returning notification preference information
 * response object with user's current notification settings
 */
public class NotificationPreferenceResponse {

    private Long id;
    // which user these preferences belong to
    private Long userId;
    // user's preference settings for each notification type
    private Boolean emailNotifications;
    private Boolean enrollmentAlerts;
    private Boolean gradeAlerts;
    private Boolean assignmentAlerts;
    private Boolean messageAlerts;
    private Boolean connectionAlerts;
    private Boolean deadlineReminders;
    // days before deadline for reminders
    private Integer reminderDaysBefore;

    // constructors
    public NotificationPreferenceResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public Boolean getEnrollmentAlerts() {
        return enrollmentAlerts;
    }

    public void setEnrollmentAlerts(Boolean enrollmentAlerts) {
        this.enrollmentAlerts = enrollmentAlerts;
    }

    public Boolean getGradeAlerts() {
        return gradeAlerts;
    }

    public void setGradeAlerts(Boolean gradeAlerts) {
        this.gradeAlerts = gradeAlerts;
    }

    public Boolean getAssignmentAlerts() {
        return assignmentAlerts;
    }

    public void setAssignmentAlerts(Boolean assignmentAlerts) {
        this.assignmentAlerts = assignmentAlerts;
    }

    public Boolean getMessageAlerts() {
        return messageAlerts;
    }

    public void setMessageAlerts(Boolean messageAlerts) {
        this.messageAlerts = messageAlerts;
    }

    public Boolean getConnectionAlerts() {
        return connectionAlerts;
    }

    public void setConnectionAlerts(Boolean connectionAlerts) {
        this.connectionAlerts = connectionAlerts;
    }

    public Boolean getDeadlineReminders() {
        return deadlineReminders;
    }

    public void setDeadlineReminders(Boolean deadlineReminders) {
        this.deadlineReminders = deadlineReminders;
    }

    public Integer getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(Integer reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }
}
