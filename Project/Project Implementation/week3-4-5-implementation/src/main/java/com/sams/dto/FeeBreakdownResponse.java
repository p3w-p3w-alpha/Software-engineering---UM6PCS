package com.sams.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FeeBreakdownResponse {

    private Long studentId;
    private String studentName;
    private Long semesterId;
    private String semesterName;
    private BigDecimal totalAmount;
    private BigDecimal totalDiscount;
    private BigDecimal totalFinalAmount;
    private List<FeeItemResponse> feeItems = new ArrayList<>();
    private List<CategoryBreakdown> categoryBreakdowns = new ArrayList<>();

    // constructors
    public FeeBreakdownResponse() {
    }

    // inner class for category breakdown
    public static class CategoryBreakdown {
        private String category;
        private BigDecimal amount;
        private BigDecimal percentage;

        public CategoryBreakdown() {
        }

        public CategoryBreakdown(String category, BigDecimal amount, BigDecimal percentage) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }
    }

    // getters and setters
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalFinalAmount() {
        return totalFinalAmount;
    }

    public void setTotalFinalAmount(BigDecimal totalFinalAmount) {
        this.totalFinalAmount = totalFinalAmount;
    }

    public List<FeeItemResponse> getFeeItems() {
        return feeItems;
    }

    public void setFeeItems(List<FeeItemResponse> feeItems) {
        this.feeItems = feeItems;
    }

    public List<CategoryBreakdown> getCategoryBreakdowns() {
        return categoryBreakdowns;
    }

    public void setCategoryBreakdowns(List<CategoryBreakdown> categoryBreakdowns) {
        this.categoryBreakdowns = categoryBreakdowns;
    }
}
