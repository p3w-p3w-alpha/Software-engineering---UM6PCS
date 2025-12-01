package com.sams.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * response object for detailed fee breakdown
 * shows complete breakdown of all fees for a student in a semester
 */
public class FeeBreakdownResponse {

    // student id
    private Long studentId;

    // student name
    private String studentName;

    // semester id
    private Long semesterId;

    // semester name
    private String semesterName;

    // total amount before discounts
    private BigDecimal totalAmount;

    // total discounts applied
    private BigDecimal totalDiscount;

    // final amount due
    private BigDecimal totalFinalAmount;

    // list of individual fee items
    private List<FeeItemResponse> feeItems = new ArrayList<>();

    // breakdown by category
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
