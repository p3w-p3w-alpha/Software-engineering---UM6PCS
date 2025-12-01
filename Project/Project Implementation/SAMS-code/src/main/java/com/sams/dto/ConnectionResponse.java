package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for user connection info
 * used for managing friend connections betwen users in teh system
 */
public class ConnectionResponse {

    // connection id
    private Long id;

    // user who initiated teh connection
    private Long requesterId;

    // requester's name
    private String requesterName;

    // requester's email
    private String requesterEmail;

    // user who recieved the request
    private Long receiverId;

    // receiver's name
    private String receiverName;

    // receiver's email
    private String receiverEmail;

    // connection status (PENDING, ACCEPTED, REJECTED, BLOCKED)
    private String status;

    // when connection was requested
    private LocalDateTime createdAt;

    // when it was accepted
    private LocalDateTime acceptedAt;

    // when it was rejected
    private LocalDateTime rejectedAt;

    // when it was blocked
    private LocalDateTime blockedAt;

    // who blocked it
    private Long blockedById;

    // blocker's name
    private String blockedByName;

    // constructors
    public ConnectionResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterEmail() {
        return requesterEmail;
    }

    public void setRequesterEmail(String requesterEmail) {
        this.requesterEmail = requesterEmail;
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

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAcceptedAt() {
        return acceptedAt;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
    }

    public LocalDateTime getRejectedAt() {
        return rejectedAt;
    }

    public void setRejectedAt(LocalDateTime rejectedAt) {
        this.rejectedAt = rejectedAt;
    }

    public LocalDateTime getBlockedAt() {
        return blockedAt;
    }

    public void setBlockedAt(LocalDateTime blockedAt) {
        this.blockedAt = blockedAt;
    }

    public Long getBlockedById() {
        return blockedById;
    }

    public void setBlockedById(Long blockedById) {
        this.blockedById = blockedById;
    }

    public String getBlockedByName() {
        return blockedByName;
    }

    public void setBlockedByName(String blockedByName) {
        this.blockedByName = blockedByName;
    }
}
