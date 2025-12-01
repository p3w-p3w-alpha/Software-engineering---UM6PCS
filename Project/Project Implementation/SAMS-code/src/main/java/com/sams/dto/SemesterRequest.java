package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * DTO for creating or updating a semester
 * used for managing academic terms and thier dates
 */
public class SemesterRequest {

    // semester name like "Fall 2024"
    @NotBlank(message = "Semester name is required")
    private String name;

    // unique semester code
    @NotBlank(message = "Semester code is required")
    private String code;

    // when teh semester starts
    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    // when the semester ends
    @NotNull(message = "End date is required")
    private LocalDate endDate;

    // when enrollment period begins
    private LocalDate enrollmentStartDate;

    // when enrollment period closes
    private LocalDate enrollmentEndDate;

    // last day students can drop courses
    private LocalDate dropDeadline;

    // deadline for faculty to submit grades
    private LocalDate gradeDeadline;

    // whether registration is currently open
    private Boolean registrationOpen;

    // constructors
    public SemesterRequest() {
    }

    public SemesterRequest(String name, String code, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.code = code;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // getters and setters
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

    public Boolean getRegistrationOpen() {
        return registrationOpen;
    }

    public void setRegistrationOpen(Boolean registrationOpen) {
        this.registrationOpen = registrationOpen;
    }
}
