package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// entity representing a student's submission for an assignment
@Entity
@Table(name = "submissions")
public class Submission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // relationship with assignment - each submission belongs to an assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    // relationship with student who submitted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "is_late", nullable = false)
    private Boolean isLate = false;

    @Column(name = "late_penalty_percent")
    private Double latePenaltyPercent = 0.0;

    // file information
    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath; // local file system path

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName; // original file name

    @Column(name = "file_size_bytes", nullable = false)
    private Long fileSizeBytes;

    @Column(name = "file_type", length = 50)
    private String fileType; // pdf, docx, etc.

    // grading information
    @Column(name = "points_earned")
    private Double pointsEarned;

    @Column(name = "feedback", length = 2000)
    private String feedback;

    @Column(name = "graded_at")
    private LocalDateTime gradedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "graded_by")
    private User gradedBy;

    @Column(nullable = false, length = 20)
    private String status = "SUBMITTED"; // SUBMITTED, GRADED, RETURNED

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (submittedAt == null) {
            submittedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // constructors
    public Submission() {
    }

    public Submission(Assignment assignment, User student, String filePath, String fileName,
                     Long fileSizeBytes, String fileType) {
        this.assignment = assignment;
        this.student = student;
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSizeBytes = fileSizeBytes;
        this.fileType = fileType;
    }

    // helper methods

    /**
     * Grade this submission with points and feedback
     */
    public void grade(Double points, String feedback, User gradedBy) {
        this.pointsEarned = points;
        this.feedback = feedback;
        this.gradedAt = LocalDateTime.now();
        this.gradedBy = gradedBy;
        this.status = "GRADED";
    }

    /**
     * Return the graded submission to student
     */
    public void returnToStudent() {
        if (!"GRADED".equals(this.status)) {
            throw new IllegalStateException("Can only return graded submissions");
        }
        this.status = "RETURNED";
    }

    /**
     * Calculate final score after applying late penalty
     */
    public Double getFinalScore() {
        if (pointsEarned == null) {
            return null;
        }

        if (latePenaltyPercent == null || latePenaltyPercent == 0.0) {
            return pointsEarned;
        }

        double penalty = pointsEarned * (latePenaltyPercent / 100.0);
        return Math.max(0.0, pointsEarned - penalty);
    }

    /**
     * Chekc if submission has been graded
     */
    public boolean isGraded() {
        return "GRADED".equals(status) || "RETURNED".equals(status);
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
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

    public User getGradedBy() {
        return gradedBy;
    }

    public void setGradedBy(User gradedBy) {
        this.gradedBy = gradedBy;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
