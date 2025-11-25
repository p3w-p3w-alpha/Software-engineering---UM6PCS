package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class FeeStructureRequest {

    @NotBlank(message = "Fee name is required")
    private String feeName;

    @NotBlank(message = "Fee code is required")
    private String feeCode;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Default amount is required")
    @Positive(message = "Default amount must be positive")
    private BigDecimal defaultAmount;

    private String description;

    private Boolean mandatory = true;

    private Boolean recurring = true;

    private Boolean active = true;

    private Long programId; // null means applies to all programs

    private Integer applicableFromSemester;

    private Integer applicableToSemester;

    // constructors
    public FeeStructureRequest() {
    }

    // getters and setters
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
}
