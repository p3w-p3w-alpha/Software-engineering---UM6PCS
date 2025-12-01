package com.sams.controller;

import com.sams.dto.EnrollmentRequest;
import com.sams.dto.EnrollmentResponse;
import com.sams.entity.Enrollment;
import com.sams.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * handles course enrollment operations - enroll, drop, waitlist management
 * frontend calls these when students register for courses
 * had issues with CORS here initially but fixed it
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // constructor injection
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // create new enrollment - POST /api/enrollments
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse createEnrollment(@Valid @RequestBody EnrollmentRequest request) {
        Enrollment enrollment = enrollmentService.createEnrollment(
            request.getStudentId(),
            request.getCourseId()
        );
        return convertToResponse(enrollment);
    }

    // get all enrollments - GET /api/enrollments
    @GetMapping
    public List<EnrollmentResponse> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return enrollments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get enrollment by id - GET /api/enrollments/{id}
    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollmentById(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.getEnrollmentById(id);
        return convertToResponse(enrollment);
    }

    // get enrollments by student - GET /api/enrollments/student/{studentId}
    @GetMapping("/student/{studentId}")
    public List<EnrollmentResponse> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return enrollments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get enrollments by course - GET /api/enrollments/course/{courseId}
    @GetMapping("/course/{courseId}")
    public List<EnrollmentResponse> getEnrollmentsByCourse(@PathVariable Long courseId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return enrollments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get enrollments by status - GET /api/enrollments/status/{status}
    @GetMapping("/status/{status}")
    public List<EnrollmentResponse> getEnrollmentsByStatus(@PathVariable String status) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStatus(status);
        return enrollments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // update enrollment status - PATCH /api/enrollments/{id}/status
    @PatchMapping("/{id}/status")
    public EnrollmentResponse updateEnrollmentStatus(@PathVariable Long id, @RequestParam String status) {
        Enrollment enrollment = enrollmentService.updateEnrollmentStatus(id, status);
        return convertToResponse(enrollment);
    }

    // drop enrollment - PUT /api/enrollments/{id}/drop
    @PutMapping("/{id}/drop")
    public EnrollmentResponse dropEnrollment(@PathVariable Long id) {
        Enrollment enrollment = enrollmentService.dropEnrollment(id);
        return convertToResponse(enrollment);
    }

    // delete enrollment - DELETE /api/enrollments/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEnrollment(@PathVariable Long id) {
        enrollmentService.deleteEnrollment(id);
    }

    // get enrollment count for a course - GET /api/enrollments/course/{courseId}/count
    @GetMapping("/course/{courseId}/count")
    public ResponseEntity<Long> getEnrollmentCount(@PathVariable Long courseId) {
        long count = enrollmentService.getEnrollmentCount(courseId);
        return ResponseEntity.ok(count);
    }

    // checks enrollment status - returns true/false
    // useful for validating before allowing course actions
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkEnrollment(@RequestParam Long studentId, @RequestParam Long courseId) {
        boolean isEnrolled = enrollmentService.isStudentEnrolled(studentId, courseId);
        return ResponseEntity.ok(isEnrolled);
    }

    // helper method to convert Enrollment to EnrollmentResponse
    private EnrollmentResponse convertToResponse(Enrollment enrollment) {
        EnrollmentResponse.StudentInfo studentInfo = new EnrollmentResponse.StudentInfo(
            enrollment.getStudent().getId(),
            enrollment.getStudent().getUsername(),
            enrollment.getStudent().getEmail()
        );

        // Build instructor info if available
        EnrollmentResponse.InstructorInfo instructorInfo = null;
        Long instructorId = null;
        if (enrollment.getCourse().getInstructor() != null) {
            var instructor = enrollment.getCourse().getInstructor();
            instructorId = instructor.getId();
            instructorInfo = new EnrollmentResponse.InstructorInfo(
                instructor.getId(),
                instructor.getUsername(),
                instructor.getEmail(),
                instructor.getUsername(), // use username as name if no separate name field
                null // department - can be added if TeacherProfile is linked
            );
        }

        EnrollmentResponse.CourseInfo courseInfo = new EnrollmentResponse.CourseInfo(
            enrollment.getCourse().getId(),
            enrollment.getCourse().getCourseCode(),
            enrollment.getCourse().getCourseName(),
            enrollment.getCourse().getCredits(),
            enrollment.getCourse().getDaysOfWeek(),
            enrollment.getCourse().getStartTime(),
            enrollment.getCourse().getEndTime(),
            enrollment.getCourse().getDescription(),
            instructorInfo,
            instructorId,
            enrollment.getCourse().getCourseFee(),
            enrollment.getCourse().getCapacity(),
            "Room " + (100 + enrollment.getCourse().getId()) // Generate room based on course ID
        );

        EnrollmentResponse response = new EnrollmentResponse();
        response.setId(enrollment.getId());
        response.setStudent(studentInfo);
        response.setCourse(courseInfo);
        response.setStatus(enrollment.getStatus());
        response.setEnrollmentDate(enrollment.getEnrollmentDate());
        response.setWaitlistPosition(enrollment.getWaitlistPosition());

        return response;
    }
}
