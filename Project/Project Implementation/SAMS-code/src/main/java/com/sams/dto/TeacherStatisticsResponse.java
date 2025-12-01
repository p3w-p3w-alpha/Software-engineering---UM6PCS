package com.sams.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO for teacher performance statistics and analytics
 */
public class TeacherStatisticsResponse {

    private Long teacherId;
    private String teacherName;
    private String department;
    private String designation;

    // Profile statistics
    private Integer totalCoursesTaught;
    private Integer totalStudentsTaught;
    private Integer currentActiveCourses;
    private Double averageRating;
    private Integer totalRatings;

    // Office hours statistics
    private Integer totalOfficeHours;
    private Integer activeOfficeHours;
    private Integer totalSlotsPerWeek;
    private Boolean availableForConsultation;

    // Weekly availability breakdown
    private Map<String, Integer> officeHoursByDay = new HashMap<>();

    // Consultation type breakdown
    private Integer inPersonOfficeHours = 0;
    private Integer onlineOfficeHours = 0;
    private Integer hybridOfficeHours = 0;

    // Experience and qualifications
    private Integer yearsOfExperience;
    private String qualification;
    private String specialization;

    // Capacity information
    private Integer maxCoursesPerSemester;
    private Integer currentCourseLoad;
    private Integer availableCourseSlots;
    private Integer maxStudentsPerCourse;

    // Profile completion
    private Boolean profileCompleted;
    private Double profileCompletionPercentage;

    // Constructors
    public TeacherStatisticsResponse() {
    }

    public TeacherStatisticsResponse(Long teacherId, String teacherName) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    // Helper methods
    public void calculateAvailableCourseSlots() {
        if (maxCoursesPerSemester != null && currentCourseLoad != null) {
            this.availableCourseSlots = Math.max(0, maxCoursesPerSemester - currentCourseLoad);
        }
    }

    public void addOfficeHoursForDay(String day, Integer hours) {
        officeHoursByDay.put(day, officeHoursByDay.getOrDefault(day, 0) + hours);
    }

    // Getters and Setters
    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
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

    public Integer getCurrentActiveCourses() {
        return currentActiveCourses;
    }

    public void setCurrentActiveCourses(Integer currentActiveCourses) {
        this.currentActiveCourses = currentActiveCourses;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Integer getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(Integer totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getTotalOfficeHours() {
        return totalOfficeHours;
    }

    public void setTotalOfficeHours(Integer totalOfficeHours) {
        this.totalOfficeHours = totalOfficeHours;
    }

    public Integer getActiveOfficeHours() {
        return activeOfficeHours;
    }

    public void setActiveOfficeHours(Integer activeOfficeHours) {
        this.activeOfficeHours = activeOfficeHours;
    }

    public Integer getTotalSlotsPerWeek() {
        return totalSlotsPerWeek;
    }

    public void setTotalSlotsPerWeek(Integer totalSlotsPerWeek) {
        this.totalSlotsPerWeek = totalSlotsPerWeek;
    }

    public Boolean getAvailableForConsultation() {
        return availableForConsultation;
    }

    public void setAvailableForConsultation(Boolean availableForConsultation) {
        this.availableForConsultation = availableForConsultation;
    }

    public Map<String, Integer> getOfficeHoursByDay() {
        return officeHoursByDay;
    }

    public void setOfficeHoursByDay(Map<String, Integer> officeHoursByDay) {
        this.officeHoursByDay = officeHoursByDay;
    }

    public Integer getInPersonOfficeHours() {
        return inPersonOfficeHours;
    }

    public void setInPersonOfficeHours(Integer inPersonOfficeHours) {
        this.inPersonOfficeHours = inPersonOfficeHours;
    }

    public Integer getOnlineOfficeHours() {
        return onlineOfficeHours;
    }

    public void setOnlineOfficeHours(Integer onlineOfficeHours) {
        this.onlineOfficeHours = onlineOfficeHours;
    }

    public Integer getHybridOfficeHours() {
        return hybridOfficeHours;
    }

    public void setHybridOfficeHours(Integer hybridOfficeHours) {
        this.hybridOfficeHours = hybridOfficeHours;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
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

    public Integer getMaxCoursesPerSemester() {
        return maxCoursesPerSemester;
    }

    public void setMaxCoursesPerSemester(Integer maxCoursesPerSemester) {
        this.maxCoursesPerSemester = maxCoursesPerSemester;
    }

    public Integer getCurrentCourseLoad() {
        return currentCourseLoad;
    }

    public void setCurrentCourseLoad(Integer currentCourseLoad) {
        this.currentCourseLoad = currentCourseLoad;
    }

    public Integer getAvailableCourseSlots() {
        return availableCourseSlots;
    }

    public void setAvailableCourseSlots(Integer availableCourseSlots) {
        this.availableCourseSlots = availableCourseSlots;
    }

    public Integer getMaxStudentsPerCourse() {
        return maxStudentsPerCourse;
    }

    public void setMaxStudentsPerCourse(Integer maxStudentsPerCourse) {
        this.maxStudentsPerCourse = maxStudentsPerCourse;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public Double getProfileCompletionPercentage() {
        return profileCompletionPercentage;
    }

    public void setProfileCompletionPercentage(Double profileCompletionPercentage) {
        this.profileCompletionPercentage = profileCompletionPercentage;
    }
}
