package com.sams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * request object for recording student payments
 * used for tracking tuition and fee payments in teh system
 */
public class PaymentRequest {

    // which student is making payment
    @NotNull(message = "Student ID is required")
    private Long studentId;

    // which semester payment is for
    @NotNull(message = "Semester ID is required")
    private Long semesterId;

    // amount being paid
    @Positive(message = "Paid amount must be positive")
    private BigDecimal paidAmount;

    // how they paid (CASH, CREDIT_CARD, BANK_TRANSFER, ONLINE)
    private String paymentMethod;

    // transaction reference number
    private String transactionReference;

    // any additional notes
    private String notes;

    // constructors
    public PaymentRequest() {
    }

    // getters and setters
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(Long semesterId) {
        this.semesterId = semesterId;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
