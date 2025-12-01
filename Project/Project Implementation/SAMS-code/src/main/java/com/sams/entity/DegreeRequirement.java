package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DegreeRequirement entity - specific requirements for a degree program
 * Examples: "Complete 30 credits of Core Courses", "Complete CS101", "Maintain 2.5 GPA"
 *
 * can be either category requirements (X credits of CORE) or specific courses
 * the specificCourse field being optional makes this flexible
 */
@Entity
@Table(name = "degree_requirements")
public class DegreeRequirement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the degree program this requirement belongs to - many requirements per program
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_program_id", nullable = false)
    private DegreeProgram degreeProgram;

    // requirement name like "Core Computer Science Courses"
    @NotBlank(message = "Requirement name is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name; // e.g., "Core Computer Science Courses"

    @Column(length = 500)
    private String description;

    // type of requirement: CORE, ELECTIVE, GENERAL_ED, MAJOR, MINOR, CAPSTONE
    @NotBlank(message = "Requirement type is required")
    @Column(nullable = false, length = 50)
    private String type;

    // number of credits required for this category
    @Min(value = 0)
    @Column(name = "credits_required")
    private Integer creditsRequired = 0;

    // specific course required (optional - null if this is a category requirement)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specific_course_id")
    private Course specificCourse;

    // if true, this requirement must be completed
    @Column(nullable = false)
    private Boolean mandatory = true;

    // order for display purposes
    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(nullable = false)
    private Boolean active = true;

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
    public DegreeRequirement() {
    }

    public DegreeRequirement(DegreeProgram degreeProgram, String name, String type, Integer creditsRequired) {
        this.degreeProgram = degreeProgram;
        this.name = name;
        this.type = type;
        this.creditsRequired = creditsRequired;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DegreeProgram getDegreeProgram() {
        return degreeProgram;
    }

    public void setDegreeProgram(DegreeProgram degreeProgram) {
        this.degreeProgram = degreeProgram;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCreditsRequired() {
        return creditsRequired;
    }

    public void setCreditsRequired(Integer creditsRequired) {
        this.creditsRequired = creditsRequired;
    }

    public Course getSpecificCourse() {
        return specificCourse;
    }

    public void setSpecificCourse(Course specificCourse) {
        this.specificCourse = specificCourse;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
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
}
