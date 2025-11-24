package com.sams.service;

import com.sams.entity.*;
import com.sams.repository.NotificationPreferenceRepository;
import com.sams.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// service to manage notifications across the entire system
// provides methods for creating notifications and checking user preferences
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationService(NotificationRepository notificationRepository,
                              NotificationPreferenceRepository preferenceRepository) {
        this.notificationRepository = notificationRepository;
        this.preferenceRepository = preferenceRepository;
    }

    // ========== ENROLLMENT NOTIFICATIONS ==========

    // notify student when successfully enrolled in a course
    public void notifyEnrollmentConfirmed(User student, Course course) {
        if (!shouldNotify(student.getId(), "ENROLLMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ENROLLMENT");
        notification.setTitle("Enrollment Confirmed");
        notification.setMessage("You have been successfully enrolled in " + course.getCourseCode() + " - " + course.getCourseName());
        notification.setActionUrl("/student/courses");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // notify student when they drop a course
    public void notifyEnrollmentDropped(User student, Course course) {
        if (!shouldNotify(student.getId(), "ENROLLMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ENROLLMENT");
        notification.setTitle("Course Dropped");
        notification.setMessage("You have dropped " + course.getCourseCode() + " - " + course.getCourseName());
        notification.setActionUrl("/student/courses");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // notify student when promoted from waitlist to enrolled
    public void notifyWaitlistPromotion(User student, Course course) {
        if (!shouldNotify(student.getId(), "WAITLIST")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("WAITLIST");
        notification.setTitle("Waitlist Promotion!");
        notification.setMessage("Great news! A spot opened up in " + course.getCourseCode() + ". You have been enrolled.");
        notification.setActionUrl("/student/courses");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // notify student of their waitlist position
    public void notifyWaitlistPosition(User student, Course course, int position) {
        if (!shouldNotify(student.getId(), "WAITLIST")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("WAITLIST");
        notification.setTitle("Added to Waitlist");
        notification.setMessage("You are #" + position + " on the waitlist for " + course.getCourseCode());
        notification.setActionUrl("/student/courses");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // ========== GRADE NOTIFICATIONS ==========

    // notify student when a new grade is posted
    public void notifyGradePosted(User student, Course course, String gradeValue) {
        if (!shouldNotify(student.getId(), "GRADE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("GRADE");
        notification.setTitle("New Grade Posted");
        notification.setMessage("Your grade for " + course.getCourseCode() + " has been posted: " + gradeValue);
        notification.setActionUrl("/student/grades");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // notify student when a grade is updated
    public void notifyGradeUpdated(User student, Course course, String oldGrade, String newGrade) {
        if (!shouldNotify(student.getId(), "GRADE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("GRADE");
        notification.setTitle("Grade Updated");
        notification.setMessage("Your grade for " + course.getCourseCode() + " was updated from " + oldGrade + " to " + newGrade);
        notification.setActionUrl("/student/grades");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // notify student when grades are finalized
    public void notifyGradesFinalized(User student, Course course) {
        if (!shouldNotify(student.getId(), "GRADE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("GRADE");
        notification.setTitle("Grades Finalized");
        notification.setMessage("Final grades for " + course.getCourseCode() + " have been posted");
        notification.setActionUrl("/student/grades");
        notification.setRelatedEntityType("Course");
        notification.setRelatedEntityId(course.getId());

        notificationRepository.save(notification);
    }

    // ========== STUDY GROUP NOTIFICATIONS ==========

    // notify user when they successfully join a study group
    public void notifyStudyGroupJoined(User user, StudyGroup group) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Joined Study Group");
        notification.setMessage("You have successfully joined the study group: " + group.getName());
        notification.setActionUrl("/study-groups/" + group.getId());
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify admin when someone requests to join private group
    public void notifyStudyGroupJoinRequest(User admin, StudyGroup group, User requester) {
        if (!shouldNotify(admin.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(admin);
        notification.setType("STUDY_GROUP");
        notification.setTitle("New Join Request");
        notification.setMessage(requester.getFirstName() + " " + requester.getLastName() +
                               " wants to join " + group.getName());
        notification.setActionUrl("/study-groups/" + group.getId() + "/requests");
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when their join request is approved
    public void notifyStudyGroupJoinApproved(User user, StudyGroup group) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Join Request Approved");
        notification.setMessage("Your request to join " + group.getName() + " has been approved!");
        notification.setActionUrl("/study-groups/" + group.getId());
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when their join request is rejected
    public void notifyStudyGroupJoinRejected(User user, StudyGroup group) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Join Request Declined");
        notification.setMessage("Your request to join " + group.getName() + " was not approved");
        notification.setActionUrl("/study-groups");
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when their role changes in a group
    public void notifyStudyGroupRoleChanged(User user, StudyGroup group, String newRole) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Role Updated");
        notification.setMessage("You are now a " + newRole + " in " + group.getName());
        notification.setActionUrl("/study-groups/" + group.getId());
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when they are removed from a group
    public void notifyStudyGroupRemoved(User user, StudyGroup group) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Removed from Group");
        notification.setMessage("You have been removed from the study group: " + group.getName());
        notification.setActionUrl("/study-groups");
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when a study group is disbanded
    public void notifyStudyGroupDisbanded(User user, StudyGroup group) {
        if (!shouldNotify(user.getId(), "STUDY_GROUP")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("STUDY_GROUP");
        notification.setTitle("Group Disbanded");
        notification.setMessage("The study group " + group.getName() + " has been disbanded");
        notification.setActionUrl("/study-groups");
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // notify user when there's a new message in a group
    public void notifyStudyGroupNewMessage(User user, StudyGroup group, User sender) {
        if (!shouldNotify(user.getId(), "MESSAGE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType("MESSAGE");
        notification.setTitle("New Group Message");
        notification.setMessage(sender.getFirstName() + " " + sender.getLastName() +
                               " sent a message in " + group.getName());
        notification.setActionUrl("/study-groups/" + group.getId() + "/chat");
        notification.setRelatedEntityType("StudyGroup");
        notification.setRelatedEntityId(group.getId());

        notificationRepository.save(notification);
    }

    // ========== ASSIGNMENT NOTIFICATIONS ==========

    // notify student when a new assignment is created
    public void notifyAssignmentCreated(User student, Course course, Assignment assignment) {
        if (!shouldNotify(student.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ASSIGNMENT");
        notification.setTitle("New Assignment Posted");
        notification.setMessage("New assignment for " + course.getCourseCode() + ": " + assignment.getTitle());
        notification.setActionUrl("/assignments/" + assignment.getId());
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify student when an assignment is updated
    public void notifyAssignmentUpdated(User student, Course course, Assignment assignment) {
        if (!shouldNotify(student.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ASSIGNMENT");
        notification.setTitle("Assignment Updated");
        notification.setMessage("Assignment updated for " + course.getCourseCode() + ": " + assignment.getTitle());
        notification.setActionUrl("/assignments/" + assignment.getId());
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify student when assignment deadline is approaching
    public void notifyAssignmentDeadlineApproaching(User student, Course course, Assignment assignment, int daysRemaining) {
        if (!shouldNotify(student.getId(), "DEADLINE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("DEADLINE");
        notification.setTitle("Assignment Deadline Approaching");
        notification.setMessage(assignment.getTitle() + " for " + course.getCourseCode() +
                               " is due in " + daysRemaining + " day(s)");
        notification.setActionUrl("/assignments/" + assignment.getId());
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify faculty when a student submits an assignment
    public void notifySubmissionReceived(User faculty, Course course, Assignment assignment, User student) {
        if (!shouldNotify(faculty.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(faculty);
        notification.setType("ASSIGNMENT");
        notification.setTitle("New Submission Received");
        notification.setMessage(student.getFirstName() + " " + student.getLastName() +
                               " submitted " + assignment.getTitle() + " for " + course.getCourseCode());
        notification.setActionUrl("/assignments/" + assignment.getId() + "/submissions");
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify student when their submission is confirmed
    public void notifySubmissionConfirmed(User student, Course course, Assignment assignment) {
        if (!shouldNotify(student.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ASSIGNMENT");
        notification.setTitle("Submission Confirmed");
        notification.setMessage("Your submission for " + assignment.getTitle() +
                               " in " + course.getCourseCode() + " was received successfully");
        notification.setActionUrl("/assignments/" + assignment.getId());
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify student when their submission is graded
    public void notifySubmissionGraded(User student, Course course, Assignment assignment, Double finalScore) {
        if (!shouldNotify(student.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ASSIGNMENT");
        notification.setTitle("Assignment Graded");
        notification.setMessage("Your submission for " + assignment.getTitle() +
                               " has been graded. Score: " + finalScore + "/" + assignment.getMaxPoints());
        notification.setActionUrl("/assignments/" + assignment.getId() + "/my-submission");
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // notify student when their graded submission is returned
    public void notifySubmissionReturned(User student, Course course, Assignment assignment) {
        if (!shouldNotify(student.getId(), "ASSIGNMENT")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(student);
        notification.setType("ASSIGNMENT");
        notification.setTitle("Graded Assignment Returned");
        notification.setMessage("Your graded assignment for " + assignment.getTitle() +
                               " in " + course.getCourseCode() + " has been returned");
        notification.setActionUrl("/assignments/" + assignment.getId() + "/my-submission");
        notification.setRelatedEntityType("Assignment");
        notification.setRelatedEntityId(assignment.getId());

        notificationRepository.save(notification);
    }

    // ========== CONNECTION NOTIFICATIONS ==========

    // notify user when they receive a connection request
    public void notifyConnectionRequest(User receiver, User requester) {
        if (!shouldNotify(receiver.getId(), "CONNECTION")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType("CONNECTION");
        notification.setTitle("New Connection Request");
        notification.setMessage(requester.getFirstName() + " " + requester.getLastName() +
                               " wants to connect with you");
        notification.setActionUrl("/connections/requests");
        notification.setRelatedEntityType("User");
        notification.setRelatedEntityId(requester.getId());

        notificationRepository.save(notification);
    }

    // notify user when their connection request is accepted
    public void notifyConnectionAccepted(User requester, User receiver) {
        if (!shouldNotify(requester.getId(), "CONNECTION")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(requester);
        notification.setType("CONNECTION");
        notification.setTitle("Connection Request Accepted");
        notification.setMessage(receiver.getFirstName() + " " + receiver.getLastName() +
                               " accepted your connection request");
        notification.setActionUrl("/connections");
        notification.setRelatedEntityType("User");
        notification.setRelatedEntityId(receiver.getId());

        notificationRepository.save(notification);
    }

    // ========== PRIVATE MESSAGE NOTIFICATIONS ==========

    // notify user when they recieve a private message
    public void notifyPrivateMessageReceived(User receiver, User sender) {
        if (!shouldNotify(receiver.getId(), "MESSAGE")) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(receiver);
        notification.setType("MESSAGE");
        notification.setTitle("New Private Message");
        notification.setMessage(sender.getFirstName() + " " + sender.getLastName() +
                               " sent you a message");
        notification.setActionUrl("/messages/" + sender.getId());
        notification.setRelatedEntityType("User");
        notification.setRelatedEntityId(sender.getId());

        notificationRepository.save(notification);
    }

    // ========== GENERIC NOTIFICATION METHODS ==========

    // create a custom notification
    @Transactional
    public Notification createNotification(User user, String type, String title, String message,
                                          String actionUrl, String entityType, Long entityId) {
        if (!shouldNotify(user.getId(), type)) {
            return null;
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        notification.setRelatedEntityType(entityType);
        notification.setRelatedEntityId(entityId);

        return notificationRepository.save(notification);
    }

    // get all notifications for a user (paginated)
    public Page<Notification> getNotificationsForUser(Long userId, Pageable pageable) {
        // we would normally get user first, but for efficiency we can use userId directly
        // in production, you might want to verify user exists first
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    // get unread notifications for a user
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndReadFalseOrderByCreatedAtDesc(userId);
    }

    // get count of unread notifications
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndReadFalse(userId);
    }

    // mark notification as read
    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + notificationId));

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    // mark all notifications as read for a user
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        for (Notification notification : unreadNotifications) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
    }

    // delete a notification
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    // delete all read notifications for a user
    @Transactional
    public void deleteReadNotifications(Long userId) {
        User user = new User();
        user.setId(userId);
        notificationRepository.deleteByUserAndReadTrue(user);
    }

    // ========== PREFERENCE MANAGEMENT ==========

    // get or create notification preferences for a user
    public NotificationPreference getOrCreatePreferences(Long userId) {
        return preferenceRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = new User();
                    user.setId(userId);
                    NotificationPreference pref = new NotificationPreference(user);
                    return preferenceRepository.save(pref);
                });
    }

    // update notification preferences
    @Transactional
    public NotificationPreference updatePreferences(Long userId, NotificationPreference preferences) {
        NotificationPreference existing = getOrCreatePreferences(userId);

        // update fields
        existing.setEmailNotifications(preferences.getEmailNotifications());
        existing.setEnrollmentAlerts(preferences.getEnrollmentAlerts());
        existing.setGradeAlerts(preferences.getGradeAlerts());
        existing.setAssignmentAlerts(preferences.getAssignmentAlerts());
        existing.setMessageAlerts(preferences.getMessageAlerts());
        existing.setConnectionAlerts(preferences.getConnectionAlerts());
        existing.setDeadlineReminders(preferences.getDeadlineReminders());
        existing.setReminderDaysBefore(preferences.getReminderDaysBefore());

        return preferenceRepository.save(existing);
    }

    // helper method to check if user should recieve a notification based on their preferences
    private boolean shouldNotify(Long userId, String notificationType) {
        NotificationPreference prefs = preferenceRepository.findByUserId(userId).orElse(null);

        // if no preferences exist, create default ones (all enabled)
        if (prefs == null) {
            return true;
        }

        return prefs.isNotificationTypeEnabled(notificationType);
    }
}
