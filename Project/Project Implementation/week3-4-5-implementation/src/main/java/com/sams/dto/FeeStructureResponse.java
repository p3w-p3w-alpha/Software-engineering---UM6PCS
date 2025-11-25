package com.sams.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FeeStructureResponse {

    private Long id;
    private String feeName;
    private String feeCode;
    private String category;
    private BigDecimal defaultAmount;
    private String description;
    private Boolean mandatory;
    private Boolean recurring;
    private Boolean active;
    private Long programId;
    private String programName;
    private Integer applicableFromSemester;
    private Integer applicableToSemester;
    private LocalDateTime createdAt;
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
