package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GradeRequest {

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotBlank(message = "Grade value is required")
    private String gradeValue; // A, A-, B+, B, etc.

    public GradeRequest() {
    }

    public GradeRequest(Long enrollmentId, String gradeValue) {
        this.enrollmentId = enrollmentId;
        this.gradeValue = gradeValue;
    }

    // getters and setters
    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(String gradeValue) {
        this.gradeValue = gradeValue;
    }
}
