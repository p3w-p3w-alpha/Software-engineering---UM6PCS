package com.sams.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FeeReportResponse {

    private String reportType; // SUMMARY, DETAILED, BY_CATEGORY, BY_STUDENT
    private Long semesterId;
    private String semesterName;
    private BigDecimal totalFeesCollected;
    private BigDecimal totalFeesOutstanding;
    private BigDecimal totalDiscountsGiven;
    private BigDecimal totalFeesWaived;
    private Long totalStudents;
    private Long studentsWithOutstandingFees;
    private Double collectionRate; // percentage of fees collected vs total
    private List<StudentFeeSummary> studentSummaries = new ArrayList<>();
    private List<CategorySummary> categorySummaries = new ArrayList<>();

    // constructors
    public FeeReportResponse() {
    }

    // inner class for student fee summary
    public static class StudentFeeSummary {
        private Long studentId;
        private String studentName;
        private String program;
        private BigDecimal totalFees;
        private BigDecimal paidAmount;
        private BigDecimal outstandingAmount;
        private String status; // PAID, PARTIAL, PENDING

        public StudentFeeSummary() {
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

        public String getProgram() {
            return program;
        }

        public void setProgram(String program) {
            this.program = program;
        }

        public BigDecimal getTotalFees() {
            return totalFees;
        }

        public void setTotalFees(BigDecimal totalFees) {
            this.totalFees = totalFees;
        }

        public BigDecimal getPaidAmount() {
            return paidAmount;
        }

        public void setPaidAmount(BigDecimal paidAmount) {
            this.paidAmount = paidAmount;
        }

        public BigDecimal getOutstandingAmount() {
            return outstandingAmount;
        }

        public void setOutstandingAmount(BigDecimal outstandingAmount) {
            this.outstandingAmount = outstandingAmount;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    // inner class for category summary
    public static class CategorySummary {
        private String category;
        private BigDecimal totalAmount;
        private BigDecimal collectedAmount;
        private BigDecimal outstandingAmount;
        private Long studentCount;

        public CategorySummary() {
        }

        public CategorySummary(String category, BigDecimal totalAmount) {
            this.category = category;
            this.totalAmount = totalAmount;
        }

        // getters and setters
        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
        }

        public BigDecimal getCollectedAmount() {
            return collectedAmount;
        }

        public void setCollectedAmount(BigDecimal collectedAmount) {
            this.collectedAmount = collectedAmount;
        }

        public BigDecimal getOutstandingAmount() {
            return outstandingAmount;
        }

        public void setOutstandingAmount(BigDecimal outstandingAmount) {
            this.outstandingAmount = outstandingAmount;
        }

        public Long getStudentCount() {
            return studentCount;
        }

        public void setStudentCount(Long studentCount) {
            this.studentCount = studentCount;
        }
    }

    // getters and setters
    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
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

    public BigDecimal getTotalFeesCollected() {
        return totalFeesCollected;
    }

    public void setTotalFeesCollected(BigDecimal totalFeesCollected) {
        this.totalFeesCollected = totalFeesCollected;
    }

    public BigDecimal getTotalFeesOutstanding() {
        return totalFeesOutstanding;
    }

    public void setTotalFeesOutstanding(BigDecimal totalFeesOutstanding) {
        this.totalFeesOutstanding = totalFeesOutstanding;
    }

    public BigDecimal getTotalDiscountsGiven() {
        return totalDiscountsGiven;
    }

    public void setTotalDiscountsGiven(BigDecimal totalDiscountsGiven) {
        this.totalDiscountsGiven = totalDiscountsGiven;
    }

    public BigDecimal getTotalFeesWaived() {
        return totalFeesWaived;
    }

    public void setTotalFeesWaived(BigDecimal totalFeesWaived) {
        this.totalFeesWaived = totalFeesWaived;
    }

    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getStudentsWithOutstandingFees() {
        return studentsWithOutstandingFees;
    }

    public void setStudentsWithOutstandingFees(Long studentsWithOutstandingFees) {
        this.studentsWithOutstandingFees = studentsWithOutstandingFees;
    }

    public Double getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(Double collectionRate) {
        this.collectionRate = collectionRate;
    }

    public List<StudentFeeSummary> getStudentSummaries() {
        return studentSummaries;
    }

    public void setStudentSummaries(List<StudentFeeSummary> studentSummaries) {
        this.studentSummaries = studentSummaries;
    }

    public List<CategorySummary> getCategorySummaries() {
        return categorySummaries;
    }

    public void setCategorySummaries(List<CategorySummary> categorySummaries) {
        this.categorySummaries = categorySummaries;
    }
}
