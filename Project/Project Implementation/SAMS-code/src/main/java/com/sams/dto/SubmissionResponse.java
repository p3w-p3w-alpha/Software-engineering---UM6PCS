package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for assignment submissions
 * used for tracking student submissions and grading responsability
 */
public class SubmissionResponse {

    // submission id
    private Long id;

    // which assignment this is for
    private Long assignmentId;

    // assignment title for display
    private String assignmentTitle;

    // student who submitted it
    private Long studentId;

    // student's full name
    private String studentName;

    // when it was submitted
    private LocalDateTime submittedAt;

    // whether submission was late
    private Boolean isLate;

    // late penalty applied (percentage)
    private Double latePenaltyPercent;

    // submitted file name
    private String fileName;

    // file size in bytes
    private Long fileSizeBytes;

    // file type/extension
    private String fileType;

    // points earned by student
    private Double pointsEarned;

    // max points possible
    private Double maxPoints;

    // final calculated score after penalties
    private Double finalScore;

    // teacher's feedback on teh submission
    private String feedback;

    // when it was graded
    private LocalDateTime gradedAt;

    // who graded it
    private Long gradedById;

    // grader's name
    private String gradedByName;

    // submission status
    private String status;

    // whether submission is active
    private Boolean active;

    // constructors
    public SubmissionResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getAssignmentTitle() {
        return assignmentTitle;
    }

    public void setAssignmentTitle(String assignmentTitle) {
        this.assignmentTitle = assignmentTitle;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Boolean getIsLate() {
        return isLate;
    }

    public void setIsLate(Boolean isLate) {
        this.isLate = isLate;
    }

    public Double getLatePenaltyPercent() {
        return latePenaltyPercent;
    }

    public void setLatePenaltyPercent(Double latePenaltyPercent) {
        this.latePenaltyPercent = latePenaltyPercent;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Double getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(Double pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public Double getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Double getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(Double finalScore) {
        this.finalScore = finalScore;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getGradedAt() {
        return gradedAt;
    }

    public void setGradedAt(LocalDateTime gradedAt) {
        this.gradedAt = gradedAt;
    }

    public Long getGradedById() {
        return gradedById;
    }

    public void setGradedById(Long gradedById) {
        this.gradedById = gradedById;
    }

    public String getGradedByName() {
        return gradedByName;
    }

    public void setGradedByName(String gradedByName) {
        this.gradedByName = gradedByName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
