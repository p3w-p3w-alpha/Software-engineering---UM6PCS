package com.sams.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for returning semester information
 * response object that gets sent to frontend with all semester details
 */
public class SemesterResponse {

    private Long id;
    // semester name like "Spring 2025"
    private String name;
    // unique semester code
    private String code;
    private LocalDate startDate;
    private LocalDate endDate;
    // enrollment window dates
    private LocalDate enrollmentStartDate;
    private LocalDate enrollmentEndDate;
    // important deadlines
    private LocalDate dropDeadline;
    private LocalDate gradeDeadline;
    // is this semester currently active
    private Boolean active;
    // can students register right now
    private Boolean registrationOpen;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // constructors
    public SemesterResponse() {
    }

    public SemesterResponse(Long id, String name, String code, LocalDate startDate, LocalDate endDate,
                           Boolean active, Boolean registrationOpen) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.registrationOpen = registrationOpen;
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
