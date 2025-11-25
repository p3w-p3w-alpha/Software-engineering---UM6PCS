package com.sams.dto.dashboard;

/**
 * DTO for grade distribution statistics
 */
public class GradeDistribution {

    private String gradeValue; // A, B, C, D, F
    private Long count;
    private Double percentage;

    // Constructors
    public GradeDistribution() {
    }

    public GradeDistribution(String gradeValue, Long count) {
        this.gradeValue = gradeValue;
        this.count = count;
    }

    public GradeDistribution(String gradeValue, Long count, Double percentage) {
        this.gradeValue = gradeValue;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters and Setters
    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
