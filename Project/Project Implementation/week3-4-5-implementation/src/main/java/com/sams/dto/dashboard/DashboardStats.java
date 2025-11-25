package com.sams.dto.dashboard;

/**
 * DTO for overall dashboard statistics
 */
public class DashboardStats {

    private Long totalStudents;
    private Long totalFaculty;
    private Long totalCourses;
    private Long totalEnrollments;
    private Long activeStudents;
    private Long inactiveStudents;
    private Long activeFaculty;
    private Long inactiveFaculty;

    // Trends (compared to previous period)
    private Double studentGrowthRate;
    private Double facultyGrowthRate;
    private Double enrollmentGrowthRate;

    // Constructors
    public DashboardStats() {
    }

    public DashboardStats(Long totalStudents, Long totalFaculty, Long totalCourses, Long totalEnrollments) {
        this.totalStudents = totalStudents;
        this.totalFaculty = totalFaculty;
        this.totalCourses = totalCourses;
        this.totalEnrollments = totalEnrollments;
    }

    // Getters and Setters
    public Long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public Long getTotalFaculty() {
        return totalFaculty;
    }

    public void setTotalFaculty(Long totalFaculty) {
        this.totalFaculty = totalFaculty;
    }

    public Long getTotalCourses() {
        return totalCourses;
    }

    public void setTotalCourses(Long totalCourses) {
        this.totalCourses = totalCourses;
    }

    public Long getTotalEnrollments() {
        return totalEnrollments;
    }

    public void setTotalEnrollments(Long totalEnrollments) {
        this.totalEnrollments = totalEnrollments;
    }

    public Long getActiveStudents() {
        return activeStudents;
    }

    public void setActiveStudents(Long activeStudents) {
        this.activeStudents = activeStudents;
    }

    public Long getInactiveStudents() {
        return inactiveStudents;
    }

    public void setInactiveStudents(Long inactiveStudents) {
        this.inactiveStudents = inactiveStudents;
    }

    public Long getActiveFaculty() {
        return activeFaculty;
    }

    public void setActiveFaculty(Long activeFaculty) {
        this.activeFaculty = activeFaculty;
    }

    public Long getInactiveFaculty() {
        return inactiveFaculty;
    }

    public void setInactiveFaculty(Long inactiveFaculty) {
        this.inactiveFaculty = inactiveFaculty;
    }

    public Double getStudentGrowthRate() {
        return studentGrowthRate;
    }

    public void setStudentGrowthRate(Double studentGrowthRate) {
        this.studentGrowthRate = studentGrowthRate;
    }

    public Double getFacultyGrowthRate() {
        return facultyGrowthRate;
    }

    public void setFacultyGrowthRate(Double facultyGrowthRate) {
        this.facultyGrowthRate = facultyGrowthRate;
    }

    public Double getEnrollmentGrowthRate() {
        return enrollmentGrowthRate;
    }

    public void setEnrollmentGrowthRate(Double enrollmentGrowthRate) {
        this.enrollmentGrowthRate = enrollmentGrowthRate;
    }
}
