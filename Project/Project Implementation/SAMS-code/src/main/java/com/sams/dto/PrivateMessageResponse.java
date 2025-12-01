package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for private messages
 * used for displaying direct messages in chat interface
 */
public class PrivateMessageResponse {

    // message id
    private Long id;

    // who sent the message
    private Long senderId;

    // sender's full name
    private String senderName;

    // who will recieve it
    private Long receiverId;

    // receiver's full name
    private String receiverName;

    // message content
    private String content;

    // when it was sent
    private LocalDateTime sentAt;

    // whether message has been read
    private Boolean read;

    // when it was read
    private LocalDateTime readAt;

    // whether message was deleted
    private Boolean deleted;

    // constructors
    public PrivateMessageResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
