package com.sams.dto;

import java.time.LocalDateTime;

/**
 * DTO for returning notification information
 * response object for in-app notifications sent to users
 */
public class NotificationResponse {

    private Long id;
    // which user recieves this notification
    private Long userId;
    // notification type (INFO, WARNING, SUCCESS, etc)
    private String type;
    // notification title/heading
    private String title;
    // main notification message
    private String message;
    // optional URL for action button
    private String actionUrl;
    // type of entity this notification is about
    private String relatedEntityType;
    // id of related entity
    private Long relatedEntityId;
    // has user read this notification
    private Boolean read;
    // when it was read
    private LocalDateTime readAt;
    // when notification was created
    private LocalDateTime createdAt;

    // constructors
    public NotificationResponse() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public void setRelatedEntityType(String relatedEntityType) {
        this.relatedEntityType = relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public void setRelatedEntityId(Long relatedEntityId) {
        this.relatedEntityId = relatedEntityId;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
