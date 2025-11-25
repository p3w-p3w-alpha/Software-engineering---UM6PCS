package com.sams.dto;

import jakarta.validation.constraints.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * DTO for creating or updating office hours
 */
public class OfficeHoursRequest {

    private Long teacherId; // Required for creation

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 500, message = "Meeting link must not exceed 500 characters")
    private String meetingLink;

    @Pattern(regexp = "IN_PERSON|ONLINE|BOTH", message = "Consultation type must be IN_PERSON, ONLINE, or BOTH")
    private String consultationType = "IN_PERSON";

    @Min(value = 1, message = "Max students per slot must be at least 1")
    private Integer maxStudentsPerSlot = 1;

    @Min(value = 5, message = "Slot duration must be at least 5 minutes")
    @Max(value = 480, message = "Slot duration must not exceed 480 minutes (8 hours)")
    private Integer slotDurationMinutes = 30;

    private Boolean bookingRequired = true;

    @Min(value = 0, message = "Advance booking days must be non-negative")
    @Max(value = 90, message = "Advance booking days must not exceed 90")
    private Integer advanceBookingDays = 7;

    private LocalDate effectiveFrom;

    private LocalDate effectiveUntil;

    private Boolean active = true;

    private Boolean recurring = true;

    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;

    // For cancellation updates
    private Boolean isCancelled = false;

    @Size(max = 200, message = "Cancellation reason must not exceed 200 characters")
    private String cancellationReason;

    private LocalDate cancelledOnDate;

    // Constructors
    public OfficeHoursRequest() {
    }

    public OfficeHoursRequest(Long teacherId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.teacherId = teacherId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Custom validation
    public boolean isValidTimeRange() {
        if (startTime == null || endTime == null) {
            return false;
        }
        return endTime.isAfter(startTime);
    }

    public boolean isValidDateRange() {
        if (effectiveFrom == null || effectiveUntil == null) {
            return true; // Both null is valid (no date restriction)
        }
        if (effectiveFrom != null && effectiveUntil != null) {
            return !effectiveUntil.isBefore(effectiveFrom);
        }
        return true; // Only one is null is valid
    }

    // Getters and Setters
    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public String getConsultationType() {
        return consultationType;
    }

    public void setConsultationType(String consultationType) {
        this.consultationType = consultationType;
    }

    public Integer getMaxStudentsPerSlot() {
        return maxStudentsPerSlot;
    }

    public void setMaxStudentsPerSlot(Integer maxStudentsPerSlot) {
        this.maxStudentsPerSlot = maxStudentsPerSlot;
    }

    public Integer getSlotDurationMinutes() {
        return slotDurationMinutes;
    }

    public void setSlotDurationMinutes(Integer slotDurationMinutes) {
        this.slotDurationMinutes = slotDurationMinutes;
    }

    public Boolean getBookingRequired() {
        return bookingRequired;
    }

    public void setBookingRequired(Boolean bookingRequired) {
        this.bookingRequired = bookingRequired;
    }

    public Integer getAdvanceBookingDays() {
        return advanceBookingDays;
    }

    public void setAdvanceBookingDays(Integer advanceBookingDays) {
        this.advanceBookingDays = advanceBookingDays;
    }

    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public LocalDate getEffectiveUntil() {
        return effectiveUntil;
    }

    public void setEffectiveUntil(LocalDate effectiveUntil) {
        this.effectiveUntil = effectiveUntil;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getRecurring() {
        return recurring;
    }

    public void setRecurring(Boolean recurring) {
        this.recurring = recurring;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public LocalDate getCancelledOnDate() {
        return cancelledOnDate;
    }

    public void setCancelledOnDate(LocalDate cancelledOnDate) {
        this.cancelledOnDate = cancelledOnDate;
    }
}
