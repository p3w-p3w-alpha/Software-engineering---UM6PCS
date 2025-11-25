package com.sams.dto.dashboard;

/**
 * DTO for enrollment trends over time
 */
public class EnrollmentTrend {

    private String period; // e.g., "2024-01", "Week 1", "Jan 2024"
    private Long enrollmentCount;
    private Long newEnrollments;
    private Long droppedEnrollments;

    // Constructors
    public EnrollmentTrend() {
    }

    public EnrollmentTrend(String period, Long enrollmentCount) {
        this.period = period;
        this.enrollmentCount = enrollmentCount;
    }

    public EnrollmentTrend(String period, Long enrollmentCount, Long newEnrollments, Long droppedEnrollments) {
        this.period = period;
        this.enrollmentCount = enrollmentCount;
        this.newEnrollments = newEnrollments;
        this.droppedEnrollments = droppedEnrollments;
    }

    // Getters and Setters
    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Long getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(Long enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public Long getNewEnrollments() {
        return newEnrollments;
    }

    public void setNewEnrollments(Long newEnrollments) {
        this.newEnrollments = newEnrollments;
    }

    public Long getDroppedEnrollments() {
        return droppedEnrollments;
    }

    public void setDroppedEnrollments(Long droppedEnrollments) {
        this.droppedEnrollments = droppedEnrollments;
    }
}
