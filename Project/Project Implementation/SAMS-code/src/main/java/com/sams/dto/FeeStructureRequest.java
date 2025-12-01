package com.sams.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * request object for creating fee structures
 * admins use this to define different types of fees for teh system
 */
public class FeeStructureRequest {

    // name of the fee (like "Tuition Fee")
    @NotBlank(message = "Fee name is required")
    private String feeName;

    // unique fee code
    @NotBlank(message = "Fee code is required")
    private String feeCode;

    // fee category (TUITION, LAB, LIBRARY, etc)
    @NotBlank(message = "Category is required")
    private String category;

    // default amount for this fee
    @NotNull(message = "Default amount is required")
    @Positive(message = "Default amount must be positive")
    private BigDecimal defaultAmount;

    // description of what fee is for
    private String description;

    // whether fee is mandatory
    private Boolean mandatory = true;

    // whether fee recurs every semester
    private Boolean recurring = true;

    // whether fee is currently active
    private Boolean active = true;

    // which program this applies to (null means all programs)
    private Long programId;

    // from which semester it applies
    private Integer applicableFromSemester;

    // until which semester it applies
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
