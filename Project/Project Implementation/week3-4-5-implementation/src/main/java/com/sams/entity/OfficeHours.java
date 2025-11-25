package com.sams.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Entity representing teacher office hours for student consultations
 * Teachers can define their availability for student meetings
 */
@Entity
@Table(name = "office_hours")
public class OfficeHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The teacher whose office hours these are
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    // Day of the week
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Time slot
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Location information
    @Column(name = "location", length = 100)
    private String location; // e.g., "Office Room 305" or "Online via Zoom"

    @Column(name = "meeting_link", length = 500)
    private String meetingLink; // For online consultations

    // Type of consultation
    @Column(name = "consultation_type", length = 20)
    private String consultationType = "IN_PERSON"; // IN_PERSON, ONLINE, BOTH

    // Capacity and booking
    @Column(name = "max_students_per_slot")
    private Integer maxStudentsPerSlot = 1; // Usually 1-on-1, but can be group

    @Column(name = "slot_duration_minutes")
    private Integer slotDurationMinutes = 30; // Duration per student slot

    @Column(name = "booking_required")
    private Boolean bookingRequired = true; // If false, it's walk-in

    @Column(name = "advance_booking_days")
    private Integer advanceBookingDays = 7; // How many days in advance students can book

    // Validity period
    @Column(name = "effective_from")
    private LocalDate effectiveFrom; // When these office hours start

    @Column(name = "effective_until")
    private LocalDate effectiveUntil; // When these office hours end (e.g., end of semester)

    // Status and notes
    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "recurring")
    private Boolean recurring = true; // If true, repeats weekly; if false, one-time

    @Column(length = 500)
    private String notes; // Additional information for students

    // Special status for exceptions
    @Column(name = "is_cancelled")
    private Boolean isCancelled = false;

    @Column(name = "cancellation_reason", length = 200)
    private String cancellationReason;

    @Column(name = "cancelled_on_date")
    private LocalDate cancelledOnDate; // For specific date cancellation

    // Audit fields
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

    // Constructors
    public OfficeHours() {
    }

    public OfficeHours(User teacher, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.teacher = teacher;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Helper methods
    public boolean isAvailableOn(LocalDate date) {
        if (!active || isCancelled) {
            return false;
        }

        if (effectiveFrom != null && date.isBefore(effectiveFrom)) {
            return false;
        }

        if (effectiveUntil != null && date.isAfter(effectiveUntil)) {
            return false;
        }

        // Check if it's cancelled on specific date
        if (cancelledOnDate != null && cancelledOnDate.equals(date)) {
            return false;
        }

        // Check if day of week matches
        return dayOfWeek.equals(date.getDayOfWeek());
    }

    public boolean overlaps(OfficeHours other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }

        return !this.endTime.isBefore(other.startTime) && !this.startTime.isAfter(other.endTime);
    }

    public int getTotalSlotsAvailable() {
        if (slotDurationMinutes == null || slotDurationMinutes == 0) {
            return 1;
        }

        int totalMinutes = (endTime.getHour() * 60 + endTime.getMinute()) -
                          (startTime.getHour() * 60 + startTime.getMinute());

        return totalMinutes / slotDurationMinutes;
    }

    public String getFormattedTimeSlot() {
        return startTime.toString() + " - " + endTime.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
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
