package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FeeStructure entity - different types of fees in the system
 * defines fee types like tuition, library, lab, hostel, exam fees etc
 *
 * the mandatory and recurring flags are useful for different fee types
 * applicability to specific programs/semesters makes this pretty flexible
 */
@Entity
@Table(name = "fee_structures")
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // fee name like "Tuition Fee" or "Library Fee" - human readable
    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String feeName;

    // fee code like "TUITION" or "LIBRARY" - used in code
    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String feeCode;

    // fee category: ACADEMIC, NON_ACADEMIC, HOSTEL, TRANSPORTATION, MISCELLANEOUS
    @NotBlank
    @Column(nullable = false, length = 50)
    private String category;

    // default amount for this fee
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal defaultAmount;

    // description of the fee
    @Column(length = 500)
    private String description;

    // whether this fee is mandatory or optional
    @Column(nullable = false)
    private Boolean mandatory = true;

    // whether this fee is one-time or recurring
    @Column(nullable = false)
    private Boolean recurring = true;

    // whether this fee is active
    @Column(nullable = false)
    private Boolean active = true;

    // applicable to which program (null means all programs)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_id")
    private DegreeProgram program;

    // applicable from which semester (null means all semesters)
    @Column(name = "applicable_from_semester")
    private Integer applicableFromSemester;

    // applicable to which semester (null means all semesters)
    @Column(name = "applicable_to_semester")
    private Integer applicableToSemester;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

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
    public FeeStructure() {
    }

    public FeeStructure(String feeName, String feeCode, String category, BigDecimal defaultAmount) {
        this.feeName = feeName;
        this.feeCode = feeCode;
        this.category = category;
        this.defaultAmount = defaultAmount;
    }

    // helper methods
    public boolean isApplicableToProgram(DegreeProgram degreeProgram) {
        return this.program == null || this.program.equals(degreeProgram);
    }

    public boolean isApplicableToSemester(int semesterNumber) {
        if (applicableFromSemester != null && semesterNumber < applicableFromSemester) {
            return false;
        }
        if (applicableToSemester != null && semesterNumber > applicableToSemester) {
            return false;
        }
        return true;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeeCode() {
        return feeCode;
    }

    public void setFeeCode(String feeCode) {
        this.feeCode = feeCode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getDefaultAmount() {
        return defaultAmount;
    }

    public void setDefaultAmount(BigDecimal defaultAmount) {
        this.defaultAmount = defaultAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public DegreeProgram getProgram() {
        return program;
    }

    public void setProgram(DegreeProgram program) {
        this.program = program;
    }

    public Integer getApplicableFromSemester() {
        return applicableFromSemester;
    }

    public void setApplicableFromSemester(Integer applicableFromSemester) {
        this.applicableFromSemester = applicableFromSemester;
    }

    public Integer getApplicableToSemester() {
        return applicableToSemester;
    }

    public void setApplicableToSemester(Integer applicableToSemester) {
        this.applicableToSemester = applicableToSemester;
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

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }
}
