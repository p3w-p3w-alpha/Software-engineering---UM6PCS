package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// entity representing an assignment created by a professor for a course
@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 2000)
    private String description;

    // relationship with course - each assignment belongs to a course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // relationship with faculty who created the assignment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "due_date", nullable = false)
    private LocalDateTime dueDate;

    @Column(name = "max_points", nullable = false)
    private Double maxPoints = 100.0;

    @Column(name = "allow_late_submissions", nullable = false)
    private Boolean allowLateSubmissions = false;

    @Column(name = "late_penalty_per_day")
    private Double latePenaltyPerDay = 10.0; // percentage penalty per day late

    @Column(name = "allowed_file_types", length = 500)
    private String allowedFileTypes; // comma-separated list: pdf,docx,zip

    @Column(name = "max_file_size_mb")
    private Integer maxFileSizeMb = 10;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // relationship with submissions - one assignment can have many submissions
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Submission> submissions = new ArrayList<>();

    // lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // constructors
    public Assignment() {
    }

    public Assignment(String title, String description, Course course, User createdBy, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.course = course;
        this.createdBy = createdBy;
        this.dueDate = dueDate;
    }

    // helper methods

    /**
     * Chekc if the assignment is currently overdue
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(dueDate);
    }

    /**
     * Calculate penalty for late submission
     * @param submissionDate the date of submission
     * @return penalty percentage (0 if not late or late submissions not allowed)
     */
    public double calculateLatePenalty(LocalDateTime submissionDate) {
        if (!allowLateSubmissions || submissionDate.isBefore(dueDate) || submissionDate.isEqual(dueDate)) {
            return 0.0;
        }

        // calculate days late (rounded up)
        long hoursLate = java.time.Duration.between(dueDate, submissionDate).toHours();
        long daysLate = (hoursLate / 24) + ((hoursLate % 24 > 0) ? 1 : 0);

        return Math.min(daysLate * latePenaltyPerDay, 100.0); // cap at 100%
    }

    /**
     * Chekc if a file type is allowed for this assignment
     */
    public boolean isFileTypeAllowed(String fileType) {
        if (allowedFileTypes == null || allowedFileTypes.isEmpty()) {
            return true; // all types allowed if not specified
        }

        String lowerFileType = fileType.toLowerCase();
        String[] allowed = allowedFileTypes.toLowerCase().split(",");

        for (String type : allowed) {
            if (type.trim().equals(lowerFileType)) {
                return true;
            }
        }

        return false;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Double getMaxPoints() {
        return maxPoints;
    }

    public void setMaxPoints(Double maxPoints) {
        this.maxPoints = maxPoints;
    }

    public Boolean getAllowLateSubmissions() {
        return allowLateSubmissions;
    }

    public void setAllowLateSubmissions(Boolean allowLateSubmissions) {
        this.allowLateSubmissions = allowLateSubmissions;
    }

    public Double getLatePenaltyPerDay() {
        return latePenaltyPerDay;
    }

    public void setLatePenaltyPerDay(Double latePenaltyPerDay) {
        this.latePenaltyPerDay = latePenaltyPerDay;
    }

    public String getAllowedFileTypes() {
        return allowedFileTypes;
    }

    public void setAllowedFileTypes(String allowedFileTypes) {
        this.allowedFileTypes = allowedFileTypes;
    }

    public Integer getMaxFileSizeMb() {
        return maxFileSizeMb;
    }

    public void setMaxFileSizeMb(Integer maxFileSizeMb) {
        this.maxFileSizeMb = maxFileSizeMb;
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

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<Submission> submissions) {
        this.submissions = submissions;
    }
}
