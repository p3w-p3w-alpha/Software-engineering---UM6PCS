package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing extended profile information for faculty members
 * One-to-one relationship with User entity (role=FACULTY)
 */
@Entity
@Table(name = "teacher_profiles")
public class TeacherProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one relationship with User (faculty member)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Academic qualifications
    @Column(length = 200)
    private String qualification; // e.g., "Ph.D. in Computer Science"

    @Column(length = 200)
    private String specialization; // e.g., "Machine Learning, AI"

    // Employment details
    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String designation; // e.g., "Professor", "Associate Professor", "Assistant Professor"

    @Column(name = "employee_id", length = 50, unique = true)
    private String employeeId;

    @Column(name = "joining_date")
    private LocalDateTime joiningDate;

    @Column(name = "years_of_experience")
    private Integer yearsOfExperience;

    // Contact and office information
    @Column(name = "office_room", length = 50)
    private String officeRoom;

    @Column(name = "office_phone", length = 20)
    private String officePhone;

    @Column(name = "office_email", length = 100)
    private String officeEmail;

    // Professional information
    @Column(length = 2000)
    private String bio; // Short biography

    @Column(name = "research_interests", length = 500)
    private String researchInterests;

    @Column(length = 500)
    private String publications; // Brief summary or count

    @Column(name = "linkedin_url", length = 200)
    private String linkedinUrl;

    @Column(name = "google_scholar_url", length = 200)
    private String googleScholarUrl;

    // Availability and preferences
    @Column(name = "max_courses_per_semester")
    private Integer maxCoursesPerSemester = 4; // Default max load

    @Column(name = "max_students_per_course")
    private Integer maxStudentsPerCourse = 40; // Default capacity

    @Column(name = "available_for_consultation")
    private Boolean availableForConsultation = true;

    // Profile status
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "profile_completed")
    private Boolean profileCompleted = false;

    // Ratings and performance (can be calculated from student feedback)
    @Column(name = "average_rating")
    private Double averageRating; // Out of 5.0

    @Column(name = "total_courses_taught")
    private Integer totalCoursesTaught = 0;

    @Column(name = "total_students_taught")
    private Integer totalStudentsTaught = 0;

    // Audit fields
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

    // Constructors
    public TeacherProfile() {
    }

    public TeacherProfile(User user) {
        this.user = user;
    }

    // Helper methods
    public boolean isProfileComplete() {
        return qualification != null && specialization != null &&
               department != null && designation != null && bio != null;
    }

    public void updateProfileCompletionStatus() {
        this.profileCompleted = isProfileComplete();
    }

    public String getFullDesignation() {
        if (designation != null && department != null) {
            return designation + ", " + department;
        }
        return designation != null ? designation : department;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
