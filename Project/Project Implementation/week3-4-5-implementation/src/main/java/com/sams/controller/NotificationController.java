package com.sams.controller;

import com.sams.dto.NotificationPreferenceRequest;
import com.sams.dto.NotificationPreferenceResponse;
import com.sams.dto.NotificationResponse;
import com.sams.entity.Notification;
import com.sams.entity.NotificationPreference;
import com.sams.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // get all notifications for current user (paginated)
    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        // Validate page size to prevent DoS attacks
        if (size < 1) size = 1;
        if (size > 100) size = 100;
        if (page < 0) page = 0;

        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> notifications = notificationService.getNotificationsForUser(userId, pageable);
        Page<NotificationResponse> responses = notifications.map(this::convertToResponse);

        return ResponseEntity.ok(responses);
    }

    // get unread notifications for current user
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponse>> getUnreadNotifications(@RequestParam Long userId) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        List<NotificationResponse> responses = notifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // get count of unread notifications
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@RequestParam Long userId) {
        long count = notificationService.getUnreadCount(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);

        return ResponseEntity.ok(response);
    }

    // mark notification as read
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        return ResponseEntity.ok(convertToResponse(notification));
    }

    // mark all notifications as read for current user
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All notifications marked as read");

        return ResponseEntity.ok(response);
    }

    // delete a notification
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // delete all read notifications for current user
    @DeleteMapping("/read")
    public ResponseEntity<Map<String, String>> deleteReadNotifications(@RequestParam Long userId) {
        notificationService.deleteReadNotifications(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "All read notifications deleted");

        return ResponseEntity.ok(response);
    }

    // ========== PREFERENCE ENDPOINTS ==========

    // get notification preferences for current user
    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> getPreferences(@RequestParam Long userId) {
        NotificationPreference preferences = notificationService.getOrCreatePreferences(userId);
        return ResponseEntity.ok(convertPreferenceToResponse(preferences));
    }

    // update notification preferences
    @PutMapping("/preferences")
    public ResponseEntity<NotificationPreferenceResponse> updatePreferences(
            @RequestParam Long userId,
            @RequestBody NotificationPreferenceRequest request) {

        NotificationPreference preferences = convertPreferenceToEntity(request);
        NotificationPreference updated = notificationService.updatePreferences(userId, preferences);

        return ResponseEntity.ok(convertPreferenceToResponse(updated));
    }

    // ========== HELPER METHODS ==========

    // convert notification entity to response dto
    private NotificationResponse convertToResponse(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setUserId(notification.getUser().getId());
        response.setType(notification.getType());
        response.setTitle(notification.getTitle());
        response.setMessage(notification.getMessage());
        response.setActionUrl(notification.getActionUrl());
        response.setRelatedEntityType(notification.getRelatedEntityType());
        response.setRelatedEntityId(notification.getRelatedEntityId());
        response.setRead(notification.getRead());
        response.setReadAt(notification.getReadAt());
        response.setCreatedAt(notification.getCreatedAt());

        return response;
    }

    // convert preference entity to response dto
    private NotificationPreferenceResponse convertPreferenceToResponse(NotificationPreference pref) {
        NotificationPreferenceResponse response = new NotificationPreferenceResponse();
        response.setId(pref.getId());
        response.setUserId(pref.getUser().getId());
        response.setEmailNotifications(pref.getEmailNotifications());
        response.setEnrollmentAlerts(pref.getEnrollmentAlerts());
        response.setGradeAlerts(pref.getGradeAlerts());
        response.setAssignmentAlerts(pref.getAssignmentAlerts());
        response.setMessageAlerts(pref.getMessageAlerts());
        response.setConnectionAlerts(pref.getConnectionAlerts());
        response.setDeadlineReminders(pref.getDeadlineReminders());
        response.setReminderDaysBefore(pref.getReminderDaysBefore());

        return response;
    }

    // convert preference request dto to entity
    private NotificationPreference convertPreferenceToEntity(NotificationPreferenceRequest request) {
        NotificationPreference pref = new NotificationPreference();
        pref.setEmailNotifications(request.getEmailNotifications());
        pref.setEnrollmentAlerts(request.getEnrollmentAlerts());
        pref.setGradeAlerts(request.getGradeAlerts());
        pref.setAssignmentAlerts(request.getAssignmentAlerts());
        pref.setMessageAlerts(request.getMessageAlerts());
        pref.setConnectionAlerts(request.getConnectionAlerts());
        pref.setDeadlineReminders(request.getDeadlineReminders());
        pref.setReminderDaysBefore(request.getReminderDaysBefore());

        return pref;
    }
}
