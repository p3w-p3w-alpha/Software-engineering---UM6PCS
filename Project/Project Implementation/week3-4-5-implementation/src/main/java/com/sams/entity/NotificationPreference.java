package com.sams.entity;

import jakarta.persistence.*;

// entity to store user preferences for notifications
// allows users to control what types of notifications they recieve
@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // one preference setting per user
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // global setting - disable all email notifications
    @Column(name = "email_notifications", nullable = false)
    private Boolean emailNotifications = true;

    // specific notification type preferences
    @Column(name = "enrollment_alerts", nullable = false)
    private Boolean enrollmentAlerts = true;

    @Column(name = "grade_alerts", nullable = false)
    private Boolean gradeAlerts = true;

    @Column(name = "assignment_alerts", nullable = false)
    private Boolean assignmentAlerts = true;

    @Column(name = "message_alerts", nullable = false)
    private Boolean messageAlerts = true;

    @Column(name = "connection_alerts", nullable = false)
    private Boolean connectionAlerts = true;

    @Column(name = "deadline_reminders", nullable = false)
    private Boolean deadlineReminders = true;

    // how many days before deadline to send reminder
    @Column(name = "reminder_days_before")
    private Integer reminderDaysBefore = 1;

    // constructors
    public NotificationPreference() {
    }

    public NotificationPreference(User user) {
        this.user = user;
    }

    // helper method to check if a specific notification type is enabled
    public boolean isNotificationTypeEnabled(String type) {
        switch (type.toUpperCase()) {
            case "ENROLLMENT":
            case "WAITLIST":
                return enrollmentAlerts;
            case "GRADE":
                return gradeAlerts;
            case "ASSIGNMENT":
                return assignmentAlerts;
            case "MESSAGE":
            case "STUDY_GROUP":
                return messageAlerts;
            case "CONNECTION":
                return connectionAlerts;
            case "DEADLINE":
                return deadlineReminders;
            default:
                return true; // system notifications are always enabled
        }
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
