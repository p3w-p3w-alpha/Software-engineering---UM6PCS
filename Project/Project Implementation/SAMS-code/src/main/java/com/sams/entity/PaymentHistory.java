package com.sams.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * PaymentHistory entity - audit trail for payment changes
 * Provides complete history of payment status changes, amounts, approvals, etc.
 *
 * definately needed this for compliance and debugging payment issues
 * captures before/after states which is super helpful for troubleshooting
 */
@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the payment this history entry belongs to - multiple entries per payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // type of action: CREATED, UPDATED, PAID, APPROVED, REJECTED, PARTIAL_PAYMENT
    @Column(nullable = false, length = 50)
    private String actionType;

    // previous status before this change
    @Column(name = "previous_status", length = 20)
    private String previousStatus;

    // new status after this change
    @Column(name = "new_status", length = 20)
    private String newStatus;

    // previous amount
    @Column(name = "previous_amount", precision = 10, scale = 2)
    private BigDecimal previousAmount;

    // new amount
    @Column(name = "new_amount", precision = 10, scale = 2)
    private BigDecimal newAmount;

    // user who performed this action (student, admin, system)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by")
    private User performedBy;

    // description or notes about this change
    @Column(length = 500)
    private String description;

    // transaction reference if applicable
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // constructors
    public PaymentHistory() {
    }

    public PaymentHistory(Payment payment, String actionType, User performedBy, String description) {
        this.payment = payment;
        this.actionType = actionType;
        this.performedBy = performedBy;
        this.description = description;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public BigDecimal getPreviousAmount() {
        return previousAmount;
    }

    public void setPreviousAmount(BigDecimal previousAmount) {
        this.previousAmount = previousAmount;
    }

    public BigDecimal getNewAmount() {
        return newAmount;
    }

    public void setNewAmount(BigDecimal newAmount) {
        this.newAmount = newAmount;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
