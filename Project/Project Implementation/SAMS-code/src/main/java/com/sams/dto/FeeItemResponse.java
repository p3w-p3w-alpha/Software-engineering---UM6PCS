package com.sams.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * response object for individual fee items
 * used for showing breakdown of fees assigned to students
 */
public class FeeItemResponse {

    // fee item id
    private Long id;

    // associated payment
    private Long paymentId;

    // which fee structure this is based on
    private Long feeStructureId;

    // fee name
    private String feeName;

    // fee code
    private String feeCode;

    // category
    private String category;

    // student id
    private Long studentId;

    // student's full name
    private String studentName;

    // semester id
    private Long semesterId;

    // semester name
    private String semesterName;

    // original amount
    private BigDecimal amount;

    // discount applied
    private BigDecimal discount;

    // final amount after discount
    private BigDecimal finalAmount;

    // reason for discount
    private String discountReason;

    // whether fee was waived
    private Boolean waived;

    // reason for waiving
    private String waiverReason;

    // any additional notes
    private String notes;

    // when created
    private LocalDateTime createdAt;

    // constructors
    public FeeItemResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Long getFeeStructureId() {
        return feeStructureId;
    }

    public void setFeeStructureId(Long feeStructureId) {
        this.feeStructureId = feeStructureId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public Boolean getWaived() {
        return waived;
    }

    public void setWaived(Boolean waived) {
        this.waived = waived;
    }

    public String getWaiverReason() {
        return waiverReason;
    }

    public void setWaiverReason(String waiverReason) {
        this.waiverReason = waiverReason;
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
}
