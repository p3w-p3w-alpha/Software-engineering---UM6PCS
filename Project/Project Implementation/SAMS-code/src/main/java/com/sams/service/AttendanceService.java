package com.sams.service;

import com.sams.dto.attendance.*;
import com.sams.entity.Attendance;
import com.sams.entity.Attendance.AttendanceStatus;
import com.sams.entity.Course;
import com.sams.entity.User;
import com.sams.repository.AttendanceRepository;
import com.sams.repository.CourseRepository;
import com.sams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * handles attendance tracking for students and faculty
 * supports bulk attendance marking and statistics generation
 *
 * attendance statuses: PRESENT, LATE, ABSENT, SICK, EXCUSED
 * calculates attendance percentages and generates reports
 *
 * works fine, could use some optimization for large classes tho
 */
@Service
@Transactional
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    /**
     * Mark attendance for a single user
     */
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        // Check if attendance already exists for this user and date
        Attendance attendance = attendanceRepository.findByUserAndDate(user, request.getDate())
                .orElse(new Attendance());

        attendance.setUser(user);
        attendance.setDate(request.getDate());
        attendance.setStatus(request.getStatus());
        attendance.setCheckInTime(request.getCheckInTime());
        attendance.setCheckOutTime(request.getCheckOutTime());
        attendance.setNotes(request.getNotes());

        if (request.getCourseId() != null) {
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + request.getCourseId()));
            attendance.setCourse(course);
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);
        return mapToResponse(savedAttendance);
    }

    /**
     * Mark attendance for multiple users (bulk operation)
     */
    public List<AttendanceResponse> markBulkAttendance(BulkAttendanceRequest request) {
        List<AttendanceResponse> responses = new ArrayList<>();

        for (AttendanceRequest attendanceRequest : request.getAttendanceRecords()) {
            try {
                AttendanceResponse response = markAttendance(attendanceRequest);
                responses.add(response);
            } catch (Exception e) {
                // Log error but continue processing other records
                System.err.println("Error marking attendance for user " + attendanceRequest.getUserId() + ": " + e.getMessage());
            }
        }

        return responses;
    }

    /**
     * Get attendance records for a specific date
     */
    public List<AttendanceResponse> getAttendanceByDate(LocalDate date) {
        List<Attendance> attendanceList = attendanceRepository.findByDate(date);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get attendance records for a date range
     */
    public List<AttendanceResponse> getAttendanceByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Attendance> attendanceList = attendanceRepository.findByDateBetween(startDate, endDate);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get attendance records for a specific user
     */
    public List<AttendanceResponse> getAttendanceByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Attendance> attendanceList = attendanceRepository.findByUserOrderByDateDesc(user);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get attendance records for a user within a date range
     */
    public List<AttendanceResponse> getAttendanceByUserAndDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        List<Attendance> attendanceList = attendanceRepository.findByUserAndDateBetweenOrderByDateDesc(user, startDate, endDate);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get attendance statistics for a specific date
     */
    public AttendanceStatistics getAttendanceStatisticsByDate(LocalDate date) {
        List<Object[]> stats = attendanceRepository.getAttendanceStatsByDate(date);
        return calculateStatistics(stats, date.toString());
    }

    /**
     * Get attendance statistics for a date range
     */
    public AttendanceStatistics getAttendanceStatisticsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = attendanceRepository.getAttendanceStatsByDateRange(startDate, endDate);
        return calculateStatistics(stats, startDate + " to " + endDate);
    }

    /**
     * Get attendance statistics for a specific user
     */
    public AttendanceStatistics getAttendanceStatisticsByUser(Long userId, LocalDate startDate, LocalDate endDate) {
        List<Object[]> stats = attendanceRepository.getAttendanceStatsByUser(userId, startDate, endDate);
        return calculateStatistics(stats, startDate + " to " + endDate);
    }

    /**
     * Get attendance statistics by user role (STUDENT, FACULTY)
     */
    public Map<String, AttendanceStatistics> getAttendanceStatisticsByRole(LocalDate startDate, LocalDate endDate) {
        Map<String, AttendanceStatistics> roleStats = new HashMap<>();

        // Get stats for students
        AttendanceStatistics studentStats = getStatisticsByRole("STUDENT", startDate, endDate);
        roleStats.put("STUDENT", studentStats);

        // Get stats for faculty
        AttendanceStatistics facultyStats = getStatisticsByRole("FACULTY", startDate, endDate);
        roleStats.put("FACULTY", facultyStats);

        return roleStats;
    }

    private AttendanceStatistics getStatisticsByRole(String role, LocalDate startDate, LocalDate endDate) {
        List<User> users = userRepository.findByRole(role);

        long totalCount = 0;
        long presentCount = 0;
        long lateCount = 0;
        long absentCount = 0;
        long sickCount = 0;
        long excusedCount = 0;

        for (User user : users) {
            List<Object[]> userStats = attendanceRepository.getAttendanceStatsByUser(user.getId(), startDate, endDate);
            for (Object[] stat : userStats) {
                AttendanceStatus status = (AttendanceStatus) stat[0];
                Long count = (Long) stat[1];

                totalCount += count;
                switch (status) {
                    case PRESENT:
                        presentCount += count;
                        break;
                    case LATE:
                        lateCount += count;
                        break;
                    case ABSENT:
                        absentCount += count;
                        break;
                    case SICK:
                        sickCount += count;
                        break;
                    case EXCUSED:
                        excusedCount += count;
                        break;
                }
            }
        }

        AttendanceStatistics statistics = new AttendanceStatistics(
            totalCount, presentCount, lateCount, absentCount, sickCount, excusedCount
        );
        statistics.setPeriod(startDate + " to " + endDate);
        return statistics;
    }

    /**
     * Get attendance percentage for a user
     */
    public Double getAttendancePercentage(Long userId, LocalDate startDate, LocalDate endDate) {
        Double percentage = attendanceRepository.getAttendancePercentage(userId, startDate, endDate);
        return percentage != null ? percentage : 0.0;
    }

    /**
     * Delete attendance record
     */
    public void deleteAttendance(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId)
                .orElseThrow(() -> new RuntimeException("Attendance record not found with ID: " + attendanceId));
        attendanceRepository.delete(attendance);
    }

    /**
     * Get attendance statistics for a specific course
     */
    public AttendanceStatistics getAttendanceStatisticsByCourse(Long courseId, LocalDate startDate, LocalDate endDate) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        List<Attendance> attendanceList = attendanceRepository.findByCourseAndDateBetween(course, startDate, endDate);

        long totalCount = attendanceList.size();
        long presentCount = attendanceList.stream().filter(a -> a.getStatus() == AttendanceStatus.PRESENT).count();
        long lateCount = attendanceList.stream().filter(a -> a.getStatus() == AttendanceStatus.LATE).count();
        long absentCount = attendanceList.stream().filter(a -> a.getStatus() == AttendanceStatus.ABSENT).count();
        long sickCount = attendanceList.stream().filter(a -> a.getStatus() == AttendanceStatus.SICK).count();
        long excusedCount = attendanceList.stream().filter(a -> a.getStatus() == AttendanceStatus.EXCUSED).count();

        AttendanceStatistics statistics = new AttendanceStatistics(
            totalCount, presentCount, lateCount, absentCount, sickCount, excusedCount
        );
        statistics.setPeriod(startDate + " to " + endDate);
        return statistics;
    }

    /**
     * Get attendance records for a specific course
     */
    public List<AttendanceResponse> getAttendanceByCourse(Long courseId, LocalDate startDate, LocalDate endDate) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));

        List<Attendance> attendanceList = attendanceRepository.findByCourseAndDateBetween(course, startDate, endDate);
        return attendanceList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // Helper method to calculate statistics from query results
    private AttendanceStatistics calculateStatistics(List<Object[]> stats, String period) {
        long totalCount = 0;
        long presentCount = 0;
        long lateCount = 0;
        long absentCount = 0;
        long sickCount = 0;
        long excusedCount = 0;

        for (Object[] stat : stats) {
            AttendanceStatus status = (AttendanceStatus) stat[0];
            Long count = (Long) stat[1];

            totalCount += count;
            switch (status) {
                case PRESENT:
                    presentCount += count;
                    break;
                case LATE:
                    lateCount += count;
                    break;
                case ABSENT:
                    absentCount += count;
                    break;
                case SICK:
                    sickCount += count;
                    break;
                case EXCUSED:
                    excusedCount += count;
                    break;
            }
        }

        AttendanceStatistics statistics = new AttendanceStatistics(
            totalCount, presentCount, lateCount, absentCount, sickCount, excusedCount
        );
        statistics.setPeriod(period);
        return statistics;
    }

    // Helper method to map Attendance entity to AttendanceResponse DTO
    private AttendanceResponse mapToResponse(Attendance attendance) {
        AttendanceResponse response = new AttendanceResponse();
        response.setId(attendance.getId());
        response.setUserId(attendance.getUser().getId());
        response.setUsername(attendance.getUser().getUsername());
        response.setFullName((attendance.getUser().getFirstName() != null ? attendance.getUser().getFirstName() : "") +
                           " " + (attendance.getUser().getLastName() != null ? attendance.getUser().getLastName() : ""));
        response.setRole(attendance.getUser().getRole());
        response.setDate(attendance.getDate());
        response.setStatus(attendance.getStatus());
        response.setCheckInTime(attendance.getCheckInTime());
        response.setCheckOutTime(attendance.getCheckOutTime());
        response.setNotes(attendance.getNotes());
        response.setCreatedAt(attendance.getCreatedAt());

        if (attendance.getCourse() != null) {
            response.setCourseId(attendance.getCourse().getId());
            response.setCourseName(attendance.getCourse().getCourseName());
        }

        return response;
    }
}
