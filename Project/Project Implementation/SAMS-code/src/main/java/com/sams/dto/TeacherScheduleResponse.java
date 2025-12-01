package com.sams.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO for teacher weekly schedule overview
 * Provides a complete view of a teacher's office hours organized by day
 */
public class TeacherScheduleResponse {

    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private String department;
    private String designation;
    private Boolean availableForConsultation;

    // Office hours organized by day of week
    private Map<String, List<OfficeHoursResponse>> scheduleByDay = new HashMap<>();

    // Summary statistics
    private Integer totalOfficeHours;
    private Integer totalSlotsPerWeek;
    private Integer activeDays; // Number of days with office hours

    // Quick availability check
    private List<String> availableDays = new ArrayList<>();
    private List<String> unavailableDays = new ArrayList<>();

    // Consultation types available
    private Boolean hasInPersonConsultation = false;
    private Boolean hasOnlineConsultation = false;
    private Boolean hasHybridConsultation = false;

    // Special notes
    private List<String> specialNotes = new ArrayList<>();
    private List<String> cancellations = new ArrayList<>();

    // Constructors
    public TeacherScheduleResponse() {
        // Initialize schedule for all days
        scheduleByDay.put("MONDAY", new ArrayList<>());
        scheduleByDay.put("TUESDAY", new ArrayList<>());
        scheduleByDay.put("WEDNESDAY", new ArrayList<>());
        scheduleByDay.put("THURSDAY", new ArrayList<>());
        scheduleByDay.put("FRIDAY", new ArrayList<>());
        scheduleByDay.put("SATURDAY", new ArrayList<>());
        scheduleByDay.put("SUNDAY", new ArrayList<>());
    }

    public TeacherScheduleResponse(Long teacherId, String teacherName) {
        this();
        this.teacherId = teacherId;
        this.teacherName = teacherName;
    }

    // Helper methods
    public void addOfficeHours(String dayOfWeek, OfficeHoursResponse officeHours) {
        if (!scheduleByDay.containsKey(dayOfWeek)) {
            scheduleByDay.put(dayOfWeek, new ArrayList<>());
        }
        scheduleByDay.get(dayOfWeek).add(officeHours);

        // Update availability
        if (!availableDays.contains(dayOfWeek)) {
            availableDays.add(dayOfWeek);
        }

        // Update consultation types
        if (officeHours.getConsultationType() != null) {
            switch (officeHours.getConsultationType()) {
                case "IN_PERSON":
                    hasInPersonConsultation = true;
                    break;
                case "ONLINE":
                    hasOnlineConsultation = true;
                    break;
                case "BOTH":
                    hasHybridConsultation = true;
                    break;
            }
        }
    }

    public void calculateStatistics() {
        totalOfficeHours = 0;
        totalSlotsPerWeek = 0;
        activeDays = 0;

        for (Map.Entry<String, List<OfficeHoursResponse>> entry : scheduleByDay.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                activeDays++;
                totalOfficeHours += entry.getValue().size();

                for (OfficeHoursResponse oh : entry.getValue()) {
                    if (oh.getTotalSlotsAvailable() != null) {
                        totalSlotsPerWeek += oh.getTotalSlotsAvailable();
                    }
                }
            }
        }

        // Determine unavailable days
        unavailableDays.clear();
        for (String day : scheduleByDay.keySet()) {
            if (!availableDays.contains(day)) {
                unavailableDays.add(day);
            }
        }
    }

    public boolean hasOfficeHoursOnDay(String dayOfWeek) {
        return scheduleByDay.containsKey(dayOfWeek) && !scheduleByDay.get(dayOfWeek).isEmpty();
    }

    public List<OfficeHoursResponse> getOfficeHoursForDay(String dayOfWeek) {
        return scheduleByDay.getOrDefault(dayOfWeek, new ArrayList<>());
    }

    // Getters and Setters
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Boolean getAvailableForConsultation() {
        return availableForConsultation;
    }

    public void setAvailableForConsultation(Boolean availableForConsultation) {
        this.availableForConsultation = availableForConsultation;
    }

    public Map<String, List<OfficeHoursResponse>> getScheduleByDay() {
        return scheduleByDay;
    }

    public void setScheduleByDay(Map<String, List<OfficeHoursResponse>> scheduleByDay) {
        this.scheduleByDay = scheduleByDay;
    }

    public Integer getTotalOfficeHours() {
        return totalOfficeHours;
    }

    public void setTotalOfficeHours(Integer totalOfficeHours) {
        this.totalOfficeHours = totalOfficeHours;
    }

    public Integer getTotalSlotsPerWeek() {
        return totalSlotsPerWeek;
    }

    public void setTotalSlotsPerWeek(Integer totalSlotsPerWeek) {
        this.totalSlotsPerWeek = totalSlotsPerWeek;
    }

    public Integer getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(Integer activeDays) {
        this.activeDays = activeDays;
    }

    public List<String> getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(List<String> availableDays) {
        this.availableDays = availableDays;
    }

    public List<String> getUnavailableDays() {
        return unavailableDays;
    }

    public void setUnavailableDays(List<String> unavailableDays) {
        this.unavailableDays = unavailableDays;
    }

    public Boolean getHasInPersonConsultation() {
        return hasInPersonConsultation;
    }

    public void setHasInPersonConsultation(Boolean hasInPersonConsultation) {
        this.hasInPersonConsultation = hasInPersonConsultation;
    }

    public Boolean getHasOnlineConsultation() {
        return hasOnlineConsultation;
    }

    public void setHasOnlineConsultation(Boolean hasOnlineConsultation) {
        this.hasOnlineConsultation = hasOnlineConsultation;
    }

    public Boolean getHasHybridConsultation() {
        return hasHybridConsultation;
    }

    public void setHasHybridConsultation(Boolean hasHybridConsultation) {
        this.hasHybridConsultation = hasHybridConsultation;
    }

    public List<String> getSpecialNotes() {
        return specialNotes;
    }

    public void setSpecialNotes(List<String> specialNotes) {
        this.specialNotes = specialNotes;
    }

    public List<String> getCancellations() {
        return cancellations;
    }

    public void setCancellations(List<String> cancellations) {
        this.cancellations = cancellations;
    }
}
