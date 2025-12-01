package com.sams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

/**
 * DTO for submitting payment (marking as paid)
 * Used when a student submits proof of payment
 */
public class PaymentSubmitRequest {

    @NotNull(message = "Paid amount is required")
    @Positive(message = "Paid amount must be positive")
    private BigDecimal paidAmount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // CASH, CREDIT_CARD, BANK_TRANSFER, ONLINE

    @NotBlank(message = "Transaction reference is required")
    private String transactionReference;

    private String notes;

    // constructors
    public PaymentSubmitRequest() {
    }

    // getters and setters
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
