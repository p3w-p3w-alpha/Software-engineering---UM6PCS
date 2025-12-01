package com.sams.dto;

import com.sams.entity.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * response object for payment information
 * used for displaying payment history and status to students and admins
 */
public class PaymentResponse {

    // payment record id
    private Long id;

    // student who made payment
    private Long studentId;

    // student's full name
    private String studentName;

    // student's email
    private String studentEmail;

    // which semester
    private Long semesterId;

    // semester name
    private String semesterName;

    // total amount due
    private BigDecimal amount;

    // amount already paid
    private BigDecimal paidAmount;

    // remaining balance
    private BigDecimal remainingAmount;

    // payment status
    private String status;

    // payment method used
    private String paymentMethod;

    // transaction reference
    private String transactionReference;

    // when payment was made
    private LocalDateTime paymentDate;

    // payment deadline
    private LocalDateTime dueDate;

    // who approved teh payment
    private Long approvedById;

    // approver's name
    private String approvedByName;

    // when it was approved
    private LocalDateTime approvalDate;

    // reason if rejected
    private String rejectionReason;

    // additional notes
    private String notes;

    // when record was created
    private LocalDateTime createdAt;

    // last update time
    private LocalDateTime updatedAt;

    // whether payment is complete
    private boolean isFullyPaid;

    // whether payment is approved
    private boolean isApproved;

    // whether payment is overdue
    private boolean isOverdue;

    // constructors
    public PaymentResponse() {
    }

    public PaymentResponse(Payment payment) {
        this.id = payment.getId();
        this.studentId = payment.getStudent().getId();
        this.studentName = payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName();
        this.studentEmail = payment.getStudent().getEmail();
        this.semesterId = payment.getSemester().getId();
        this.semesterName = payment.getSemester().getName();
        this.amount = payment.getAmount();
        this.paidAmount = payment.getPaidAmount();
        this.remainingAmount = payment.getRemainingAmount();
        this.status = payment.getStatus();
        this.paymentMethod = payment.getPaymentMethod();
        this.transactionReference = payment.getTransactionReference();
        this.paymentDate = payment.getPaymentDate();
        this.dueDate = payment.getDueDate();

        if (payment.getApprovedBy() != null) {
            this.approvedById = payment.getApprovedBy().getId();
            this.approvedByName = payment.getApprovedBy().getFirstName() + " " +
                                 payment.getApprovedBy().getLastName();
        }

        this.approvalDate = payment.getApprovalDate();
        this.rejectionReason = payment.getRejectionReason();
        this.notes = payment.getNotes();
        this.createdAt = payment.getCreatedAt();
        this.updatedAt = payment.getUpdatedAt();
        this.isFullyPaid = payment.isFullyPaid();
        this.isApproved = payment.isApproved();
        this.isOverdue = payment.isOverdue();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public String getSemesterName() {
        return semesterName;
    }

    public void setSemesterName(String semesterName) {
        this.semesterName = semesterName;
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

    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
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

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getApprovedById() {
        return approvedById;
    }

    public void setApprovedById(Long approvedById) {
        this.approvedById = approvedById;
    }

    public String getApprovedByName() {
        return approvedByName;
    }

    public void setApprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
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

    public boolean isFullyPaid() {
        return isFullyPaid;
    }

    public void setFullyPaid(boolean fullyPaid) {
        isFullyPaid = fullyPaid;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }
}
