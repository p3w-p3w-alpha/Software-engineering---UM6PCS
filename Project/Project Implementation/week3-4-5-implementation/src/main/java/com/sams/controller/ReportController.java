package com.sams.controller;

import com.sams.entity.*;
import com.sams.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public ReportController(
            EnrollmentRepository enrollmentRepository,
            PaymentRepository paymentRepository,
            AttendanceRepository attendanceRepository,
            GradeRepository gradeRepository,
            UserRepository userRepository,
            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
        this.attendanceRepository = attendanceRepository;
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping("/enrollments")
    public List<Map<String, Object>> getEnrollmentsReport() {
        try {
            return enrollmentRepository.findAll().stream()
                    .map(enrollment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", enrollment.getId());
                        map.put("studentName", enrollment.getStudent().getFirstName() + " " + enrollment.getStudent().getLastName());
                        map.put("courseName", enrollment.getCourse().getCourseName());
                        map.put("status", enrollment.getStatus());
                        map.put("enrollmentDate", enrollment.getEnrollmentDate());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Return empty list if there's an error
            return new ArrayList<>();
        }
    }

    @GetMapping("/fees")
    public List<Map<String, Object>> getFeesReport() {
        try {
            return paymentRepository.findAll().stream()
                    .map(payment -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", payment.getId());
                        map.put("studentName", payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
                        map.put("amount", payment.getAmount());
                        map.put("status", payment.getStatus());
                        map.put("dueDate", payment.getDueDate());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/attendance")
    public List<Map<String, Object>> getAttendanceReport() {
        try {
            return attendanceRepository.findAll().stream()
                    .map(attendance -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", attendance.getId());
                        map.put("studentName", attendance.getUser().getFirstName() + " " +
                                attendance.getUser().getLastName());
                        map.put("courseName", attendance.getCourse() != null ? attendance.getCourse().getCourseName() : "N/A");
                        map.put("date", attendance.getDate());
                        map.put("status", attendance.getStatus());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/grades")
    public List<Map<String, Object>> getGradesReport() {
        try {
            return gradeRepository.findAll().stream()
                    .map(grade -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", grade.getId());
                        map.put("studentName", grade.getStudent().getFirstName() + " " +
                                grade.getStudent().getLastName());
                        map.put("courseName", grade.getCourse().getCourseName());
                        map.put("gradeValue", grade.getGradeValue());
                        map.put("gradePoints", grade.getGradePoints());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/users")
    public List<Map<String, Object>> getUsersReport(@RequestParam(required = false) String role) {
        try {
            List<User> users;
            if (role != null && !role.isEmpty()) {
                users = userRepository.findByRole(role);
            } else {
                users = userRepository.findAll();
            }

            return users.stream()
                    .map(user -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", user.getId());
                        map.put("username", user.getUsername());
                        map.put("firstName", user.getFirstName());
                        map.put("lastName", user.getLastName());
                        map.put("email", user.getEmail());
                        map.put("role", user.getRole());
                        map.put("active", user.getActive());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("/courses")
    public List<Map<String, Object>> getCoursesReport() {
        try {
            return courseRepository.findAll().stream()
                    .map(course -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", course.getId());
                        map.put("courseCode", course.getCourseCode());
                        map.put("courseName", course.getCourseName());
                        map.put("credits", course.getCredits());
                        map.put("capacity", course.getCapacity());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
