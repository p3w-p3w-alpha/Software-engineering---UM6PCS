package com.sams.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

// request DTO for grading a submission
public class GradeSubmissionRequest {

    @NotNull(message = "Points earned is required")
    @PositiveOrZero(message = "Points must be zero or positive")
    private Double pointsEarned;

    @Size(max = 2000, message = "Feedback cannot exceed 2000 characters")
    private String feedback;

    // constructors
    public GradeSubmissionRequest() {
    }

    public GradeSubmissionRequest(Double pointsEarned, String feedback) {
        this.pointsEarned = pointsEarned;
        this.feedback = feedback;
    }

    // getters and setters
    public Double getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
