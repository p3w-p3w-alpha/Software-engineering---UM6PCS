package com.sams.dto;

import com.sams.entity.OfficeHours;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * DTO for returning office hours information
 */
public class OfficeHoursResponse {

    private Long id;
    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
    private String meetingLink;
    private String consultationType;
    private Integer maxStudentsPerSlot;
    private Integer slotDurationMinutes;
    private Boolean bookingRequired;
    private Integer advanceBookingDays;
    private LocalDate effectiveFrom;
    private LocalDate effectiveUntil;
    private Boolean active;
    private Boolean recurring;
    private String notes;
    private Boolean isCancelled;
    private String cancellationReason;
    private LocalDate cancelledOnDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Computed fields
    private Integer totalSlotsAvailable;
    private String formattedTimeSlot;

    // Constructors
    public OfficeHoursResponse() {
    }

    /**
     * Creates a response DTO from an OfficeHours entity
     */
    public static OfficeHoursResponse fromEntity(OfficeHours officeHours) {
        OfficeHoursResponse response = new OfficeHoursResponse();
        response.setId(officeHours.getId());
        response.setTeacherId(officeHours.getTeacher().getId());
        response.setTeacherName(officeHours.getTeacher().getFirstName() + " " +
                                officeHours.getTeacher().getLastName());
        response.setTeacherEmail(officeHours.getTeacher().getEmail());
        response.setDayOfWeek(officeHours.getDayOfWeek());
        response.setStartTime(officeHours.getStartTime());
        response.setEndTime(officeHours.getEndTime());
        response.setLocation(officeHours.getLocation());
        response.setMeetingLink(officeHours.getMeetingLink());
        response.setConsultationType(officeHours.getConsultationType());
        response.setMaxStudentsPerSlot(officeHours.getMaxStudentsPerSlot());
        response.setSlotDurationMinutes(officeHours.getSlotDurationMinutes());
        response.setBookingRequired(officeHours.getBookingRequired());
        response.setAdvanceBookingDays(officeHours.getAdvanceBookingDays());
        response.setEffectiveFrom(officeHours.getEffectiveFrom());
        response.setEffectiveUntil(officeHours.getEffectiveUntil());
        response.setActive(officeHours.getActive());
        response.setRecurring(officeHours.getRecurring());
        response.setNotes(officeHours.getNotes());
        response.setIsCancelled(officeHours.getIsCancelled());
        response.setCancellationReason(officeHours.getCancellationReason());
        response.setCancelledOnDate(officeHours.getCancelledOnDate());
        response.setCreatedAt(officeHours.getCreatedAt());
        response.setUpdatedAt(officeHours.getUpdatedAt());

        // Set computed fields
        response.setTotalSlotsAvailable(officeHours.getTotalSlotsAvailable());
        response.setFormattedTimeSlot(officeHours.getFormattedTimeSlot());

        return response;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
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

    public Integer getTotalSlotsAvailable() {
        return totalSlotsAvailable;
    }

    public void setTotalSlotsAvailable(Integer totalSlotsAvailable) {
        this.totalSlotsAvailable = totalSlotsAvailable;
    }

    public String getFormattedTimeSlot() {
        return formattedTimeSlot;
    }

    public void setFormattedTimeSlot(String formattedTimeSlot) {
        this.formattedTimeSlot = formattedTimeSlot;
    }
}
