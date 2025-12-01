package com.sams.dto;

import com.sams.entity.TeacherProfile;

import java.time.LocalDateTime;

/**
 * DTO for returning teacher profile information
 */
public class TeacherProfileResponse {

    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String qualification;
    private String specialization;
    private String department;
    private String designation;
    private String employeeId;
    private LocalDateTime joiningDate;
    private Integer yearsOfExperience;
    private String officeRoom;
    private String officePhone;
    private String officeEmail;
    private String bio;
    private String researchInterests;
    private String publications;
    private String linkedinUrl;
    private String googleScholarUrl;
    private Integer maxCoursesPerSemester;
    private Integer maxStudentsPerCourse;
    private Boolean availableForConsultation;
    private Boolean active;
    private Boolean profileCompleted;
    private Double averageRating;
    private Integer totalCoursesTaught;
    private Integer totalStudentsTaught;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public TeacherProfileResponse() {
    }

    /**
     * Creates a response DTO from a TeacherProfile entity
     */
    public static TeacherProfileResponse fromEntity(TeacherProfile profile) {
        TeacherProfileResponse response = new TeacherProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setUserName(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());
        response.setUserEmail(profile.getUser().getEmail());
        response.setQualification(profile.getQualification());
        response.setSpecialization(profile.getSpecialization());
        response.setDepartment(profile.getDepartment());
        response.setDesignation(profile.getDesignation());
        response.setEmployeeId(profile.getEmployeeId());
        response.setJoiningDate(profile.getJoiningDate());
        response.setYearsOfExperience(profile.getYearsOfExperience());
        response.setOfficeRoom(profile.getOfficeRoom());
        response.setOfficePhone(profile.getOfficePhone());
        response.setOfficeEmail(profile.getOfficeEmail());
        response.setBio(profile.getBio());
        response.setResearchInterests(profile.getResearchInterests());
        response.setPublications(profile.getPublications());
        response.setLinkedinUrl(profile.getLinkedinUrl());
        response.setGoogleScholarUrl(profile.getGoogleScholarUrl());
        response.setMaxCoursesPerSemester(profile.getMaxCoursesPerSemester());
        response.setMaxStudentsPerCourse(profile.getMaxStudentsPerCourse());
        response.setAvailableForConsultation(profile.getAvailableForConsultation());
        response.setActive(profile.getActive());
        response.setProfileCompleted(profile.getProfileCompleted());
        response.setAverageRating(profile.getAverageRating());
        response.setTotalCoursesTaught(profile.getTotalCoursesTaught());
        response.setTotalStudentsTaught(profile.getTotalStudentsTaught());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalCoursesTaught() {
        return totalCoursesTaught;
    }

    public void setTotalCoursesTaught(Integer totalCoursesTaught) {
        this.totalCoursesTaught = totalCoursesTaught;
    }

    public Integer getTotalStudentsTaught() {
        return totalStudentsTaught;
    }

    public void setTotalStudentsTaught(Integer totalStudentsTaught) {
        this.totalStudentsTaught = totalStudentsTaught;
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
