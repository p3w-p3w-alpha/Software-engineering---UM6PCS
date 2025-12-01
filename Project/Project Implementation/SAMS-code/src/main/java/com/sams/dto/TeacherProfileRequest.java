package com.sams.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO for creating or updating a teacher profile
 */
public class TeacherProfileRequest {

    private Long userId; // Required for creation, identifies the faculty user

    @NotBlank(message = "Qualification is required")
    @Size(max = 200, message = "Qualification must not exceed 200 characters")
    private String qualification;

    @NotBlank(message = "Specialization is required")
    @Size(max = 200, message = "Specialization must not exceed 200 characters")
    private String specialization;

    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;

    @NotBlank(message = "Designation is required")
    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @Size(max = 50, message = "Employee ID must not exceed 50 characters")
    private String employeeId;

    private LocalDateTime joiningDate;

    @Min(value = 0, message = "Years of experience must be non-negative")
    private Integer yearsOfExperience;

    @Size(max = 50, message = "Office room must not exceed 50 characters")
    private String officeRoom;

    @Size(max = 20, message = "Office phone must not exceed 20 characters")
    private String officePhone;

    @Email(message = "Office email must be valid")
    @Size(max = 100, message = "Office email must not exceed 100 characters")
    private String officeEmail;

    @Size(max = 2000, message = "Bio must not exceed 2000 characters")
    private String bio;

    @Size(max = 500, message = "Research interests must not exceed 500 characters")
    private String researchInterests;

    @Size(max = 500, message = "Publications must not exceed 500 characters")
    private String publications;

    @Size(max = 200, message = "LinkedIn URL must not exceed 200 characters")
    private String linkedinUrl;

    @Size(max = 200, message = "Google Scholar URL must not exceed 200 characters")
    private String googleScholarUrl;

    @Min(value = 1, message = "Max courses per semester must be at least 1")
    private Integer maxCoursesPerSemester = 4;

    @Min(value = 1, message = "Max students per course must be at least 1")
    private Integer maxStudentsPerCourse = 40;

    private Boolean availableForConsultation = true;

    private Boolean active = true;

    // Constructors
    public TeacherProfileRequest() {
    }

    public TeacherProfileRequest(Long userId, String qualification, String specialization,
                                 String department, String designation) {
        this.userId = userId;
        this.qualification = qualification;
        this.specialization = specialization;
        this.department = department;
        this.designation = designation;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDateTime joiningDate) {
        this.joiningDate = joiningDate;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getOfficeEmail() {
        return officeEmail;
    }

    public void setOfficeEmail(String officeEmail) {
        this.officeEmail = officeEmail;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getResearchInterests() {
        return researchInterests;
    }

    public void setResearchInterests(String researchInterests) {
        this.researchInterests = researchInterests;
    }

    public String getPublications() {
        return publications;
    }

    public void setPublications(String publications) {
        this.publications = publications;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public void setLinkedinUrl(String linkedinUrl) {
        this.linkedinUrl = linkedinUrl;
    }

    public String getGoogleScholarUrl() {
        return googleScholarUrl;
    }

    public void setGoogleScholarUrl(String googleScholarUrl) {
        this.googleScholarUrl = googleScholarUrl;
    }

    public Integer getMaxCoursesPerSemester() {
        return maxCoursesPerSemester;
    }

    public void setMaxCoursesPerSemester(Integer maxCoursesPerSemester) {
        this.maxCoursesPerSemester = maxCoursesPerSemester;
    }

    public Integer getMaxStudentsPerCourse() {
        return maxStudentsPerCourse;
    }

    public void setMaxStudentsPerCourse(Integer maxStudentsPerCourse) {
        this.maxStudentsPerCourse = maxStudentsPerCourse;
    }

    public Boolean getAvailableForConsultation() {
        return availableForConsultation;
    }

    public void setAvailableForConsultation(Boolean availableForConsultation) {
        this.availableForConsultation = availableForConsultation;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
