package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for group messages
 * used for displaying messages in teh study group chat interface
 */
public class GroupMessageResponse {

    // message id
    private Long id;

    // which group this message belongs to
    private Long groupId;

    // who sent it
    private Long senderId;

    // sender's full name for display
    private String senderName;

    // the message content
    private String content;

    // message type (TEXT, FILE, IMAGE, LINK)
    private String messageType;

    // file url if it's a file message
    private String fileUrl;

    // filename for file messages
    private String fileName;

    // whether message was deleted
    private Boolean deleted;

    // when it was sent
    private LocalDateTime sentAt;

    // when it was deleted (if deleted)
    private LocalDateTime deletedAt;

    // constructors
    public GroupMessageResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
