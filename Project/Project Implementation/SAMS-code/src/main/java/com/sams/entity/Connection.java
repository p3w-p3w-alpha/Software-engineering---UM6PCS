package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Connection entity - friendship/colleague relationship between two users
 * handles connection requests, acceptence, rejection, and blocking
 *
 * kind of like linkedin connections - users can connect with eachother
 * the getOtherUser method is handy for showing connections in UI
 */
@Entity
@Table(name = "connections")
public class Connection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user who initiated teh connection request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;

    // user who received the connection request
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    // connection status - PENDING until receiver accepts/rejects
    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, ACCEPTED, REJECTED, BLOCKED

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "blocked_at")
    private LocalDateTime blockedAt;

    // who blocked (requester or receiver) - only relevant if status is BLOCKED
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_by")
    private User blockedBy;

    // lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // constructors
    public Connection() {
    }

    public Connection(User requester, User receiver) {
        this.requester = requester;
        this.receiver = receiver;
        this.status = "PENDING";
    }

    // helper methods

    /**
     * Accept the connection request
     */
    public void accept() {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Can only accept pending connection requests");
        }
        this.status = "ACCEPTED";
        this.acceptedAt = LocalDateTime.now();
    }

    /**
     * Reject the connection request
     */
    public void reject() {
        if (!"PENDING".equals(this.status)) {
            throw new IllegalStateException("Can only reject pending connection requests");
        }
        this.status = "REJECTED";
        this.rejectedAt = LocalDateTime.now();
    }

    /**
     * Block a user
     */
    public void block(User blocker) {
        this.status = "BLOCKED";
        this.blockedAt = LocalDateTime.now();
        this.blockedBy = blocker;
    }

    /**
     * Chekc if connection is active (accepted)
     */
    public boolean isActive() {
        return "ACCEPTED".equals(this.status);
    }

    /**
     * Chekc if connection is blocked
     */
    public boolean isBlocked() {
        return "BLOCKED".equals(this.status);
    }

    /**
     * Chekc if user is involved in this connection
     */
    public boolean involvesUser(Long userId) {
        return (requester != null && requester.getId().equals(userId)) ||
               (receiver != null && receiver.getId().equals(userId));
    }

    /**
     * Get the other user in the connection
     */
    public User getOtherUser(Long currentUserId) {
        if (requester != null && requester.getId().equals(currentUserId)) {
            return receiver;
        } else if (receiver != null && receiver.getId().equals(currentUserId)) {
            return requester;
        }
        return null;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getRequester() {
        return requester;
    }

    public void setRequester(User requester) {
        this.requester = requester;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
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

    public User getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(User blockedBy) {
        this.blockedBy = blockedBy;
    }
}
