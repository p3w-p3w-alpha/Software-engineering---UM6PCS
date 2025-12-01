package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DegreeProgram entity - academic degree programs like BS CS, BA Economics etc
 * defines the requirements students must complete to graduate
 *
 * this entity is pretty straightforward - basically a container for degree requirements
 * the relationship with StudentDegreeProgress lets us track student progress
 */
@Entity
@Table(name = "degree_programs")
public class DegreeProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // short code like "BSCS" or "BAECO" - used in reports and references
    @NotBlank(message = "Program code is required")
    @Size(max = 20)
    @Column(nullable = false, unique = true, length = 20)
    private String code; // e.g., "BSCS", "BAECO"

    // full program name - human readable
    @NotBlank(message = "Program name is required")
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String name; // e.g., "Bachelor of Science in Computer Science"

    @Column(length = 1000)
    private String description;

    // total credits required to graduate
    @Min(value = 1, message = "Total credits must be at least 1")
    @Column(name = "total_credits_required", nullable = false)
    private Integer totalCreditsRequired = 120; // default 120 credits

    // minimum GPA required to graduate
    @Column(name = "minimum_gpa")
    private Double minimumGpa = 2.0; // default 2.0 GPA

    // department or school offering this program
    @Column(length = 100)
    private String department;

    // typical duration in semesters
    @Column(name = "typical_duration_semesters")
    private Integer typicalDurationSemesters = 8; // 4 years = 8 semesters

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // relationships

    // requirements for this degree program
    @OneToMany(mappedBy = "degreeProgram", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DegreeRequirement> requirements = new ArrayList<>();

    // students enrolled in this program
    @OneToMany(mappedBy = "degreeProgram", cascade = CascadeType.ALL)
    private List<StudentDegreeProgress> studentProgresses = new ArrayList<>();

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
    public DegreeProgram() {
    }

    public DegreeProgram(String code, String name, Integer totalCreditsRequired) {
        this.code = code;
        this.name = name;
        this.totalCreditsRequired = totalCreditsRequired;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Integer getTotalCreditsRequired() {
        return totalCreditsRequired;
    }

    public void setTotalCreditsRequired(Integer totalCreditsRequired) {
        this.totalCreditsRequired = totalCreditsRequired;
    }

    public Double getMinimumGpa() {
        return minimumGpa;
    }

    public void setMinimumGpa(Double minimumGpa) {
        this.minimumGpa = minimumGpa;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Integer getTypicalDurationSemesters() {
        return typicalDurationSemesters;
    }

    public void setTypicalDurationSemesters(Integer typicalDurationSemesters) {
        this.typicalDurationSemesters = typicalDurationSemesters;
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

    public List<DegreeRequirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(List<DegreeRequirement> requirements) {
        this.requirements = requirements;
    }

    public List<StudentDegreeProgress> getStudentProgresses() {
        return studentProgresses;
    }

    public void setStudentProgresses(List<StudentDegreeProgress> studentProgresses) {
        this.studentProgresses = studentProgresses;
    }
}
