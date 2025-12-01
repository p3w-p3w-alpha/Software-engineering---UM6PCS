package com.sams.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * response object for fee structures
 * used for displaying configured fees and thier details
 */
public class FeeStructureResponse {

    // fee structure id
    private Long id;

    // fee name
    private String feeName;

    // fee code
    private String feeCode;

    // category
    private String category;

    // default amount
    private BigDecimal defaultAmount;

    // fee description
    private String description;

    // whether mandatory
    private Boolean mandatory;

    // whether recurring
    private Boolean recurring;

    // whether active
    private Boolean active;

    // applicable program id
    private Long programId;

    // program name for display
    private String programName;

    // from semester
    private Integer applicableFromSemester;

    // to semester
    private Integer applicableToSemester;

    // when created
    private LocalDateTime createdAt;

    // last updated
    private LocalDateTime updatedAt;

    // constructors
    public FeeStructureResponse() {
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

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
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
}
