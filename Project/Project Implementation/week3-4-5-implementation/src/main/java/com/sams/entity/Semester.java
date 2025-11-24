package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// entity to manage academic semesters/terms
// helps control enrollment periods and academic scheduling
@Entity
@Table(name = "semesters")
public class Semester {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // semester name like "Fall 2024" or "Spring 2025"
    @NotBlank(message = "Semester name is required")
    @Column(nullable = false, length = 50)
    private String name;

    // short code like "F24" or "S25"
    @NotBlank(message = "Semester code is required")
    @Column(nullable = false, unique = true, length = 10)
    private String code;

    // when the semester starts
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    // when the semester ends
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // when students can start enrolling
    @Column(name = "enrollment_start_date")
    private LocalDate enrollmentStartDate;

    // when enrollment period closes
    @Column(name = "enrollment_end_date")
    private LocalDate enrollmentEndDate;

    // last day students can drop courses
    @Column(name = "drop_deadline")
    private LocalDate dropDeadline;

    // deadline for faculty to submit grades
    @Column(name = "grade_deadline")
    private LocalDate gradeDeadline;

    // is this the current active semester?
    @Column(nullable = false)
    private Boolean active = false;

    // is registration currently open?
    @Column(name = "registration_open", nullable = false)
    private Boolean registrationOpen = false;

    // courses belonging to this semester
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL)
    private List<Course> courses = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
    public Semester() {
    }

    public Semester(String name, String code, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // helper method to check if enrollment is currently open
    public boolean isEnrollmentPeriod() {
        if (!registrationOpen || enrollmentStartDate == null || enrollmentEndDate == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return !today.isBefore(enrollmentStartDate) && !today.isAfter(enrollmentEndDate);
    }

    // helper method to check if within drop period
    public boolean isWithinDropPeriod() {
        if (dropDeadline == null) {
            return false;
        }
        return !LocalDate.now().isAfter(dropDeadline);
    }

    // helper method to chekc if within grade submission period
    public boolean isWithinGradeSubmissionPeriod() {
        if (gradeDeadline == null) {
            return true; // no deadline set, always allow
        }
        return !LocalDate.now().isAfter(gradeDeadline);
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getEnrollmentStartDate() {
        return enrollmentStartDate;
    }

    public void setEnrollmentStartDate(LocalDate enrollmentStartDate) {
        this.enrollmentStartDate = enrollmentStartDate;
    }

    public LocalDate getEnrollmentEndDate() {
        return enrollmentEndDate;
    }

    public void setEnrollmentEndDate(LocalDate enrollmentEndDate) {
        this.enrollmentEndDate = enrollmentEndDate;
    }

    public LocalDate getDropDeadline() {
        return dropDeadline;
    }

    public void setDropDeadline(LocalDate dropDeadline) {
        this.dropDeadline = dropDeadline;
    }

    public LocalDate getGradeDeadline() {
        return gradeDeadline;
    }

    public void setGradeDeadline(LocalDate gradeDeadline) {
        this.gradeDeadline = gradeDeadline;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(Boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
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
