package com.sams.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FeeItem entity - individual fee item within a payment
 * breaks down payment into specific components like tuition, library, lab fees etc
 *
 * discounts and waivers make this entity more complex but necessary
 * the auto-recalculation of finalAmount in PreUpdate is handy
 */
@Entity
@Table(name = "fee_items")
public class FeeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // the payment this fee item belongs to - many items per payment
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    // the fee structure this item is based on - defines the fee type
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_structure_id", nullable = false)
    private FeeStructure feeStructure;

    // the student this fee is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    // the semester this fee is for
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    // amount for this specific fee
    @NotNull
    @Positive
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    // discount applied to this fee (if any)
    @Column(precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ZERO;

    // final amount after discount
    @NotNull
    @Column(name = "final_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal finalAmount;

    // discount reason
    @Column(name = "discount_reason", length = 200)
    private String discountReason;

    // waived completely (scholarship, exemption, etc.)
    @Column(nullable = false)
    private Boolean waived = false;

    // waiver reason
    @Column(name = "waiver_reason", length = 200)
    private String waiverReason;

    // notes about this fee item
    @Column(length = 500)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        // calculate final amount
        if (waived) {
            finalAmount = BigDecimal.ZERO;
        } else {
            finalAmount = amount.subtract(discount != null ? discount : BigDecimal.ZERO);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();

        // recalculate final amount
        if (waived) {
            finalAmount = BigDecimal.ZERO;
        } else {
            finalAmount = amount.subtract(discount != null ? discount : BigDecimal.ZERO);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }
    }

    // constructors
    public FeeItem() {
    }

    public FeeItem(Payment payment, FeeStructure feeStructure, User student,
                   Semester semester, BigDecimal amount) {
        this.payment = payment;
        this.feeStructure = feeStructure;
        this.student = student;
        this.semester = semester;
        this.amount = amount;
        this.finalAmount = amount;
    }

    // helper methods
    public void applyDiscount(BigDecimal discountAmount, String reason) {
        this.discount = discountAmount;
        this.discountReason = reason;
        recalculateFinalAmount();
    }

    public void waiveFee(String reason) {
        this.waived = true;
        this.waiverReason = reason;
        this.finalAmount = BigDecimal.ZERO;
    }

    public void removeWaiver() {
        this.waived = false;
        this.waiverReason = null;
        recalculateFinalAmount();
    }

    private void recalculateFinalAmount() {
        if (waived) {
            finalAmount = BigDecimal.ZERO;
        } else {
            finalAmount = amount.subtract(discount != null ? discount : BigDecimal.ZERO);
            if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
                finalAmount = BigDecimal.ZERO;
            }
        }
    }

    public BigDecimal getEffectiveAmount() {
        return waived ? BigDecimal.ZERO : finalAmount;
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public FeeStructure getFeeStructure() {
        return feeStructure;
    }

    public void setFeeStructure(FeeStructure feeStructure) {
        this.feeStructure = feeStructure;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public Semester getSemester() {
        return semester;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(BigDecimal finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public Boolean getWaived() {
        return waived;
    }

    public void setWaived(Boolean waived) {
        this.waived = waived;
    }

    public String getWaiverReason() {
        return waiverReason;
    }

    public void setWaiverReason(String waiverReason) {
        this.waiverReason = waiverReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
