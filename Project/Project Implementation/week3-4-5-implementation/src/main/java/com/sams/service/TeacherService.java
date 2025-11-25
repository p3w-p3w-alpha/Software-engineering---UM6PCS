package com.sams.service;

import com.sams.dto.*;
import com.sams.entity.OfficeHours;
import com.sams.entity.TeacherProfile;
import com.sams.entity.User;
import com.sams.repository.OfficeHoursRepository;
import com.sams.repository.TeacherProfileRepository;
import com.sams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing teacher profiles and office hours
 */
@Service
@Transactional
public class TeacherService {

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private OfficeHoursRepository officeHoursRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== Teacher Profile Management ====================

    /**
     * Create a new teacher profile
     */
    public TeacherProfileResponse createTeacherProfile(TeacherProfileRequest request) {
        // Validate user exists and is faculty
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));

        if (!"FACULTY".equals(user.getRole())) {
            throw new IllegalArgumentException("User must have FACULTY role to create teacher profile");
        }

        // Check if profile already exists
        if (teacherProfileRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException("Teacher profile already exists for this user");
        }

        // Check employee ID uniqueness if provided
        if (request.getEmployeeId() != null &&
            teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + request.getEmployeeId());
        }

        // Create profile
        TeacherProfile profile = new TeacherProfile();
        profile.setUser(user);
        updateProfileFromRequest(profile, request);

        profile = teacherProfileRepository.save(profile);
        return TeacherProfileResponse.fromEntity(profile);
    }

    /**
     * Update an existing teacher profile
     */
    public TeacherProfileResponse updateTeacherProfile(Long profileId, TeacherProfileRequest request) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found with ID: " + profileId));

        // Check employee ID uniqueness if changed
        if (request.getEmployeeId() != null &&
            !request.getEmployeeId().equals(profile.getEmployeeId()) &&
            teacherProfileRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new IllegalArgumentException("Employee ID already exists: " + request.getEmployeeId());
        }

        updateProfileFromRequest(profile, request);
        profile.updateProfileCompletionStatus();

        profile = teacherProfileRepository.save(profile);
        return TeacherProfileResponse.fromEntity(profile);
    }

    /**
     * Get teacher profile by ID
     */
    public TeacherProfileResponse getTeacherProfile(Long profileId) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found with ID: " + profileId));
        return TeacherProfileResponse.fromEntity(profile);
    }

    /**
     * Get teacher profile by user ID
     */
    public TeacherProfileResponse getTeacherProfileByUserId(Long userId) {
        TeacherProfile profile = teacherProfileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user ID: " + userId));
        return TeacherProfileResponse.fromEntity(profile);
    }

    /**
     * Get all teacher profiles
     */
    public List<TeacherProfileResponse> getAllTeacherProfiles() {
        return teacherProfileRepository.findAll().stream()
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get active teacher profiles
     */
    public List<TeacherProfileResponse> getActiveTeacherProfiles() {
        return teacherProfileRepository.findByActiveTrue().stream()
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Search teachers by keyword
     */
    public List<TeacherProfileResponse> searchTeachers(String searchTerm) {
        return teacherProfileRepository.searchTeachers(searchTerm).stream()
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get teachers by department
     */
    public List<TeacherProfileResponse> getTeachersByDepartment(String department) {
        return teacherProfileRepository.findByDepartment(department).stream()
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get teachers by filters
     */
    public List<TeacherProfileResponse> getTeachersByFilters(
            String department, String designation, Boolean active, Boolean availableForConsultation) {
        return teacherProfileRepository.findByFilters(department, designation, active, availableForConsultation)
            .stream()
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get top-rated teachers
     */
    public List<TeacherProfileResponse> getTopRatedTeachers() {
        return teacherProfileRepository.findTopRatedTeachers().stream()
            .limit(10)
            .map(TeacherProfileResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Delete teacher profile
     */
    public void deleteTeacherProfile(Long profileId) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found with ID: " + profileId));

        // Delete associated office hours
        officeHoursRepository.deleteByTeacherId(profile.getUser().getId());

        // Delete profile
        teacherProfileRepository.delete(profile);
    }

    /**
     * Toggle teacher profile active status
     */
    public TeacherProfileResponse toggleTeacherProfileStatus(Long profileId) {
        TeacherProfile profile = teacherProfileRepository.findById(profileId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found with ID: " + profileId));

        profile.setActive(!profile.getActive());
        profile = teacherProfileRepository.save(profile);

        return TeacherProfileResponse.fromEntity(profile);
    }

    // ==================== Office Hours Management ====================

    /**
     * Create office hours
     */
    public OfficeHoursResponse createOfficeHours(OfficeHoursRequest request) {
        // Validate teacher exists
        User teacher = userRepository.findById(request.getTeacherId())
            .orElseThrow(() -> new IllegalArgumentException("Teacher not found with ID: " + request.getTeacherId()));

        // Validate time range
        if (!request.isValidTimeRange()) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Validate date range
        if (!request.isValidDateRange()) {
            throw new IllegalArgumentException("Effective until date must be after effective from date");
        }

        // Check for overlapping office hours
        List<OfficeHours> overlapping = officeHoursRepository.findOverlappingOfficeHoursForCreation(
            request.getTeacherId(),
            request.getDayOfWeek(),
            request.getStartTime(),
            request.getEndTime()
        );

        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Office hours overlap with existing schedule");
        }

        // Create office hours
        OfficeHours officeHours = new OfficeHours();
        officeHours.setTeacher(teacher);
        updateOfficeHoursFromRequest(officeHours, request);

        officeHours = officeHoursRepository.save(officeHours);
        return OfficeHoursResponse.fromEntity(officeHours);
    }

    /**
     * Update office hours
     */
    public OfficeHoursResponse updateOfficeHours(Long officeHoursId, OfficeHoursRequest request) {
        OfficeHours officeHours = officeHoursRepository.findById(officeHoursId)
            .orElseThrow(() -> new IllegalArgumentException("Office hours not found with ID: " + officeHoursId));

        // Validate time range
        if (!request.isValidTimeRange()) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // Validate date range
        if (!request.isValidDateRange()) {
            throw new IllegalArgumentException("Effective until date must be after effective from date");
        }

        // Check for overlapping office hours (excluding current)
        List<OfficeHours> overlapping = officeHoursRepository.findOverlappingOfficeHours(
            officeHours.getTeacher().getId(),
            request.getDayOfWeek(),
            request.getStartTime(),
            request.getEndTime(),
            officeHoursId
        );

        if (!overlapping.isEmpty()) {
            throw new IllegalArgumentException("Office hours overlap with existing schedule");
        }

        updateOfficeHoursFromRequest(officeHours, request);
        officeHours = officeHoursRepository.save(officeHours);

        return OfficeHoursResponse.fromEntity(officeHours);
    }

    /**
     * Get office hours by ID
     */
    public OfficeHoursResponse getOfficeHours(Long officeHoursId) {
        OfficeHours officeHours = officeHoursRepository.findById(officeHoursId)
            .orElseThrow(() -> new IllegalArgumentException("Office hours not found with ID: " + officeHoursId));
        return OfficeHoursResponse.fromEntity(officeHours);
    }

    /**
     * Get all office hours for a teacher
     */
    public List<OfficeHoursResponse> getTeacherOfficeHours(Long teacherId) {
        return officeHoursRepository.findByTeacherId(teacherId).stream()
            .map(OfficeHoursResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get active office hours for a teacher
     */
    public List<OfficeHoursResponse> getActiveTeacherOfficeHours(Long teacherId) {
        return officeHoursRepository.findByTeacherIdAndActiveTrueAndIsCancelledFalse(teacherId).stream()
            .map(OfficeHoursResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Get office hours available on a specific date
     */
    public List<OfficeHoursResponse> getAvailableOfficeHoursForDate(Long teacherId, LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return officeHoursRepository.findEffectiveOfficeHoursForDate(teacherId, dayOfWeek, date).stream()
            .map(OfficeHoursResponse::fromEntity)
            .collect(Collectors.toList());
    }

    /**
     * Cancel office hours
     */
    public OfficeHoursResponse cancelOfficeHours(Long officeHoursId, String reason, LocalDate specificDate) {
        OfficeHours officeHours = officeHoursRepository.findById(officeHoursId)
            .orElseThrow(() -> new IllegalArgumentException("Office hours not found with ID: " + officeHoursId));

        officeHours.setIsCancelled(true);
        officeHours.setCancellationReason(reason);
        if (specificDate != null) {
            officeHours.setCancelledOnDate(specificDate);
        }

        officeHours = officeHoursRepository.save(officeHours);
        return OfficeHoursResponse.fromEntity(officeHours);
    }

    /**
     * Reactivate cancelled office hours
     */
    public OfficeHoursResponse reactivateOfficeHours(Long officeHoursId) {
        OfficeHours officeHours = officeHoursRepository.findById(officeHoursId)
            .orElseThrow(() -> new IllegalArgumentException("Office hours not found with ID: " + officeHoursId));

        officeHours.setIsCancelled(false);
        officeHours.setCancellationReason(null);
        officeHours.setCancelledOnDate(null);

        officeHours = officeHoursRepository.save(officeHours);
        return OfficeHoursResponse.fromEntity(officeHours);
    }

    /**
     * Delete office hours
     */
    public void deleteOfficeHours(Long officeHoursId) {
        if (!officeHoursRepository.existsById(officeHoursId)) {
            throw new IllegalArgumentException("Office hours not found with ID: " + officeHoursId);
        }
        officeHoursRepository.deleteById(officeHoursId);
    }

    // ==================== Statistics and Analytics ====================

    /**
     * Get teacher statistics
     */
    public TeacherStatisticsResponse getTeacherStatistics(Long teacherId) {
        // Get teacher profile
        TeacherProfile profile = teacherProfileRepository.findByUserId(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user ID: " + teacherId));

        TeacherStatisticsResponse stats = new TeacherStatisticsResponse();
        stats.setTeacherId(teacherId);
        stats.setTeacherName(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());
        stats.setDepartment(profile.getDepartment());
        stats.setDesignation(profile.getDesignation());

        // Profile statistics
        stats.setTotalCoursesTaught(profile.getTotalCoursesTaught());
        stats.setTotalStudentsTaught(profile.getTotalStudentsTaught());
        stats.setAverageRating(profile.getAverageRating());
        stats.setYearsOfExperience(profile.getYearsOfExperience());
        stats.setQualification(profile.getQualification());
        stats.setSpecialization(profile.getSpecialization());
        stats.setMaxCoursesPerSemester(profile.getMaxCoursesPerSemester());
        stats.setMaxStudentsPerCourse(profile.getMaxStudentsPerCourse());
        stats.setProfileCompleted(profile.getProfileCompleted());
        stats.setAvailableForConsultation(profile.getAvailableForConsultation());

        // Office hours statistics
        List<OfficeHours> allOfficeHours = officeHoursRepository.findByTeacherId(teacherId);
        List<OfficeHours> activeOfficeHours = officeHoursRepository.findByTeacherIdAndActiveTrue(teacherId);

        stats.setTotalOfficeHours(allOfficeHours.size());
        stats.setActiveOfficeHours(activeOfficeHours.size());

        // Calculate total slots per week
        int totalSlots = activeOfficeHours.stream()
            .mapToInt(OfficeHours::getTotalSlotsAvailable)
            .sum();
        stats.setTotalSlotsPerWeek(totalSlots);

        // Office hours by day
        activeOfficeHours.forEach(oh -> {
            String day = oh.getDayOfWeek().toString();
            stats.addOfficeHoursForDay(day, 1);
        });

        // Count by consultation type
        for (OfficeHours oh : activeOfficeHours) {
            String type = oh.getConsultationType();
            if ("IN_PERSON".equals(type)) {
                stats.setInPersonOfficeHours(stats.getInPersonOfficeHours() + 1);
            } else if ("ONLINE".equals(type)) {
                stats.setOnlineOfficeHours(stats.getOnlineOfficeHours() + 1);
            } else if ("BOTH".equals(type)) {
                stats.setHybridOfficeHours(stats.getHybridOfficeHours() + 1);
            }
        }

        // Calculate available course slots (would need course enrollment data)
        stats.setCurrentCourseLoad(0); // Placeholder
        stats.calculateAvailableCourseSlots();

        return stats;
    }

    /**
     * Get teacher weekly schedule
     */
    public TeacherScheduleResponse getTeacherSchedule(Long teacherId) {
        // Get teacher profile
        TeacherProfile profile = teacherProfileRepository.findByUserId(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("Teacher profile not found for user ID: " + teacherId));

        TeacherScheduleResponse schedule = new TeacherScheduleResponse();
        schedule.setTeacherId(teacherId);
        schedule.setTeacherName(profile.getUser().getFirstName() + " " + profile.getUser().getLastName());
        schedule.setTeacherEmail(profile.getUser().getEmail());
        schedule.setDepartment(profile.getDepartment());
        schedule.setDesignation(profile.getDesignation());
        schedule.setAvailableForConsultation(profile.getAvailableForConsultation());

        // Get active office hours
        List<OfficeHours> activeOfficeHours = officeHoursRepository.findByTeacherIdAndActiveTrueAndIsCancelledFalse(teacherId);

        // Organize by day
        for (OfficeHours oh : activeOfficeHours) {
            OfficeHoursResponse ohResponse = OfficeHoursResponse.fromEntity(oh);
            schedule.addOfficeHours(oh.getDayOfWeek().toString(), ohResponse);

            // Collect special notes
            if (oh.getNotes() != null && !oh.getNotes().isEmpty()) {
                schedule.getSpecialNotes().add(oh.getDayOfWeek() + ": " + oh.getNotes());
            }
        }

        // Calculate statistics
        schedule.calculateStatistics();

        return schedule;
    }

    // ==================== Helper Methods ====================

    private void updateProfileFromRequest(TeacherProfile profile, TeacherProfileRequest request) {
        profile.setQualification(request.getQualification());
        profile.setSpecialization(request.getSpecialization());
        profile.setDepartment(request.getDepartment());
        profile.setDesignation(request.getDesignation());
        profile.setEmployeeId(request.getEmployeeId());
        profile.setJoiningDate(request.getJoiningDate());
        profile.setYearsOfExperience(request.getYearsOfExperience());
        profile.setOfficeRoom(request.getOfficeRoom());
        profile.setOfficePhone(request.getOfficePhone());
        profile.setOfficeEmail(request.getOfficeEmail());
        profile.setBio(request.getBio());
        profile.setResearchInterests(request.getResearchInterests());
        profile.setPublications(request.getPublications());
        profile.setLinkedinUrl(request.getLinkedinUrl());
        profile.setGoogleScholarUrl(request.getGoogleScholarUrl());
        profile.setMaxCoursesPerSemester(request.getMaxCoursesPerSemester());
        profile.setMaxStudentsPerCourse(request.getMaxStudentsPerCourse());
        profile.setAvailableForConsultation(request.getAvailableForConsultation());
        profile.setActive(request.getActive());
    }

    private void updateOfficeHoursFromRequest(OfficeHours officeHours, OfficeHoursRequest request) {
        officeHours.setDayOfWeek(request.getDayOfWeek());
        officeHours.setStartTime(request.getStartTime());
        officeHours.setEndTime(request.getEndTime());
        officeHours.setLocation(request.getLocation());
        officeHours.setMeetingLink(request.getMeetingLink());
        officeHours.setConsultationType(request.getConsultationType());
        officeHours.setMaxStudentsPerSlot(request.getMaxStudentsPerSlot());
        officeHours.setSlotDurationMinutes(request.getSlotDurationMinutes());
        officeHours.setBookingRequired(request.getBookingRequired());
        officeHours.setAdvanceBookingDays(request.getAdvanceBookingDays());
        officeHours.setEffectiveFrom(request.getEffectiveFrom());
        officeHours.setEffectiveUntil(request.getEffectiveUntil());
        officeHours.setActive(request.getActive());
        officeHours.setRecurring(request.getRecurring());
        officeHours.setNotes(request.getNotes());
    }
}
