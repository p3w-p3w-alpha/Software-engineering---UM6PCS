package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Payment entity - handles student fee payments for semesters
 * Students pay upfront per semester before they can finalize their enrollment
 *
 * this was a pain to implement - had to handle partial payments and approvals
 * also BigDecimal for money is annoying but necesary for precision
 */
@Entity
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_student", columnList = "student_id"),
    @Index(name = "idx_payment_semester", columnList = "semester_id"),
    @Index(name = "idx_payment_status", columnList = "status"),
    @Index(name = "idx_payment_student_semester", columnList = "student_id, semester_id")
})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the student who made the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // the semester this payment is for - payments are per semester
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    // total amount to be paid (sum of all enrolled course fees)
    // using BigDecimal because Double has precision issues with money
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // amount actually paid - can be less than total for partial payments
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    // payment status: PENDING, PAID, APPROVED, REJECTED, PARTIAL
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    // payment method: CASH, CREDIT_CARD, BANK_TRANSFER, ONLINE
    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    // transaction reference number (for tracking and reconcilliation)
    @Column(name = "transaction_reference", length = 100)
    private String transactionReference;

    // payment date
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // admin who approved/rejected the payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    // reason for rejection (if applicable)
    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    // notes or comments
    @Column(length = 1000)
    private String notes;

    // due date for payment
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // list of enrollments covered by this payment
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Enrollment> enrollments = new ArrayList<>();

    // payment history for audit trail
    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentHistory> paymentHistory = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // constructors
    public Payment() {
    }

    public Payment(User student, Semester semester, BigDecimal amount) {
        this.student = student;
        this.semester = semester;
        this.amount = amount;
        this.paidAmount = BigDecimal.ZERO;
        this.status = "PENDING";
    }

    // helper methods

    /**
     * Mark payment as paid
     */
    public void markAsPaid(BigDecimal paidAmount, String paymentMethod, String transactionRef) {
        this.paidAmount = paidAmount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionRef;
        this.paymentDate = LocalDateTime.now();

        if (this.paidAmount.compareTo(this.amount) >= 0) {
            this.status = "PAID";
        } else {
            this.status = "PARTIAL";
        }
    }

    /**
     * Approve payment (admin action)
     */
    public void approve(User admin) {
        this.status = "APPROVED";
        this.approvedBy = admin;
        this.approvalDate = LocalDateTime.now();
    }

    /**
     * Reject payment (admin action)
     */
    public void reject(User admin, String reason) {
        this.status = "REJECTED";
        this.approvedBy = admin;
        this.approvalDate = LocalDateTime.now();
        this.rejectionReason = reason;
    }

    /**
     * Check if payment is fully paid
     */
    public boolean isFullyPaid() {
        return paidAmount != null && paidAmount.compareTo(amount) >= 0;
    }

    /**
     * Check if payment is approved
     */
    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    /**
     * Check if payment is overdue
     */
    public boolean isOverdue() {
        if (dueDate == null || isApproved()) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDate) && !"APPROVED".equals(status);
    }

    /**
     * Get remaining amount to be paid
     */
    public BigDecimal getRemainingAmount() {
        if (amount == null || paidAmount == null) {
            return amount != null ? amount : BigDecimal.ZERO;
        }
        return amount.subtract(paidAmount);
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public User getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(User approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public List<PaymentHistory> getPaymentHistory() {
        return paymentHistory;
    }

    public void setPaymentHistory(List<PaymentHistory> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return id != null && Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
