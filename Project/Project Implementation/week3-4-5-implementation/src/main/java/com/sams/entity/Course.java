package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20)
    @Column(unique = true, nullable = false)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100)
    @Column(nullable = false)
    private String courseName;

    @Column(length = 500)
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Column(nullable = false)
    private Integer credits = 3; // default credits

    @Min(value = 1, message = "Capacity must be at least 1")
    @Column(nullable = false)
    private Integer capacity = 30; // default capacity

    // course fee (for billing/payment integration)
    @Column(name = "course_fee", precision = 10, scale = 2)
    private java.math.BigDecimal courseFee = new java.math.BigDecimal("500.00"); // default fee

    // relationship with instructor (faculty user)
    // many courses can be taught by one instructor
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instructor_id")
    private User instructor;

    // relationship with semester - each course belongs to a semester
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id")
    private Semester semester;

    // relationship with enrollments
    // one course can have many enrollments
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    // prerequisets - courses that must be completed before taking this course
    // self-referencing many-to-many relationship
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "course_prerequisites",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "prerequisite_id")
    )
    private Set<Course> prerequisites = new HashSet<>();

    // schedule information for conflict detection
    // days of week: format like "MON,WED,FRI" or "TUE,THU"
    @Column(length = 50)
    private String daysOfWeek;

    // start time for the class (e.g., 09:00)
    @Column(name = "start_time")
    private LocalTime startTime;

    // end time for the class (e.g., 10:30)
    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // soft delete fields - for keeping historical data instead of hard deletes
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy; // id of user who deleted this record

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // soft delete method - marks record as deleted without removing from database
    public void softDelete(Long deletedByUserId) {
        this.active = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedByUserId;
    }

    // constructors
    public Course() {
    }

    public Course(String courseCode, String courseName, String description, Integer credits, Integer capacity) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.description = description;
        this.credits = credits;
        this.capacity = capacity;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    // Alias method for backward compatibility
    public Integer getCreditHours() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public User getInstructor() {
        return instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
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

    public Set<Course> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(Set<Course> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(String daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    // helper method to get enrolled student count
    public int getEnrolledCount() {
        return enrollments != null ? enrollments.size() : 0;
    }

    // helper method to check if course is full
    public boolean isFull() {
        return getEnrolledCount() >= capacity;
    }

    // helper method to add prerequesite course
    public void addPrerequisite(Course prerequisite) {
        this.prerequisites.add(prerequisite);
    }

    // helper method to remove prerequesite course
    public void removePrerequisite(Course prerequisite) {
        this.prerequisites.remove(prerequisite);
    }

    // helper method to chekc if course has schedule
    public boolean hasSchedule() {
        return daysOfWeek != null && startTime != null && endTime != null;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public java.math.BigDecimal getCourseFee() {
        return courseFee;
    }

    public void setCourseFee(java.math.BigDecimal courseFee) {
        this.courseFee = courseFee;
    }
}
