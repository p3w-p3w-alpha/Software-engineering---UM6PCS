package com.sams.dto.dashboard;

import java.math.BigDecimal;

/**
 * DTO for financial summary statistics
 */
public class FinancialSummary {

    private BigDecimal totalRevenue;
    private BigDecimal pendingPayments;
    private BigDecimal completedPayments;
    private BigDecimal rejectedPayments;
    private Long totalPaymentCount;
    private Long pendingPaymentCount;
    private Long completedPaymentCount;
    private Long rejectedPaymentCount;
    private Double collectionRate; // percentage of completed payments

    // Constructors
    public FinancialSummary() {
        this.totalRevenue = BigDecimal.ZERO;
        this.pendingPayments = BigDecimal.ZERO;
        this.completedPayments = BigDecimal.ZERO;
        this.rejectedPayments = BigDecimal.ZERO;
        this.totalPaymentCount = 0L;
        this.pendingPaymentCount = 0L;
        this.completedPaymentCount = 0L;
        this.rejectedPaymentCount = 0L;
        this.collectionRate = 0.0;
    }

    // Getters and Setters
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getPendingPayments() {
        return pendingPayments;
    }

    public void setPendingPayments(BigDecimal pendingPayments) {
        this.pendingPayments = pendingPayments;
    }

    public BigDecimal getCompletedPayments() {
        return completedPayments;
    }

    public void setCompletedPayments(BigDecimal completedPayments) {
        this.completedPayments = completedPayments;
    }

    public BigDecimal getRejectedPayments() {
        return rejectedPayments;
    }

    public void setRejectedPayments(BigDecimal rejectedPayments) {
        this.rejectedPayments = rejectedPayments;
    }

    public Long getTotalPaymentCount() {
        return totalPaymentCount;
    }

    public void setTotalPaymentCount(Long totalPaymentCount) {
        this.totalPaymentCount = totalPaymentCount;
    }

    public Long getPendingPaymentCount() {
        return pendingPaymentCount;
    }

    public void setPendingPaymentCount(Long pendingPaymentCount) {
        this.pendingPaymentCount = pendingPaymentCount;
    }

    public Long getCompletedPaymentCount() {
        return completedPaymentCount;
    }

    public void setCompletedPaymentCount(Long completedPaymentCount) {
        this.completedPaymentCount = completedPaymentCount;
    }

    public Long getRejectedPaymentCount() {
        return rejectedPaymentCount;
    }

    public void setRejectedPaymentCount(Long rejectedPaymentCount) {
        this.rejectedPaymentCount = rejectedPaymentCount;
    }

    public Double getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(Double collectionRate) {
        this.collectionRate = collectionRate;
    }
}
