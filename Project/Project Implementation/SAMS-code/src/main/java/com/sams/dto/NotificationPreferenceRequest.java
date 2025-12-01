package com.sams.dto;

/**
 * DTO for updating notification preferences
 * users can customize which notifications they want to recieve
 */
public class NotificationPreferenceRequest {

    // enable/disable email notifications
    private Boolean emailNotifications;
    // alerts about enrollments
    private Boolean enrollmentAlerts;
    // alerts when grades are posted
    private Boolean gradeAlerts;
    // alerts about new assignments
    private Boolean assignmentAlerts;
    // alerts for messages from other users
    private Boolean messageAlerts;
    // alerts about connection requests
    private Boolean connectionAlerts;
    // reminders about upcoming deadlines
    private Boolean deadlineReminders;
    // how many days before deadline to send reminder
    private Integer reminderDaysBefore;

    // constructors
    public NotificationPreferenceRequest() {
    }

    // getters and setters
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
