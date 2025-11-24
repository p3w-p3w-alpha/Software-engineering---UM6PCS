package com.sams.controller;

import com.sams.dto.GradeRequest;
import com.sams.dto.GradeResponse;
import com.sams.entity.Grade;
import com.sams.service.GradeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    // constructor injection
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    // assign grade to enrollment - POST /api/grades
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GradeResponse assignGrade(@Valid @RequestBody GradeRequest request) {
        // TODO: Get current user ID from security context instead of using null
        Grade grade = gradeService.assignGrade(request.getEnrollmentId(), request.getGradeValue(), null);
        return convertToResponse(grade);
    }

    // get all grades - GET /api/grades
    @GetMapping
    public List<GradeResponse> getAllGrades() {
        List<Grade> grades = gradeService.getAllGrades();
        return grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get grade by id - GET /api/grades/{id}
    @GetMapping("/{id}")
    public GradeResponse getGradeById(@PathVariable Long id) {
        Grade grade = gradeService.getGradeById(id);
        return convertToResponse(grade);
    }

    // get grades for a student - GET /api/grades/student/{studentId}
    @GetMapping("/student/{studentId}")
    public List<GradeResponse> getGradesByStudent(@PathVariable Long studentId) {
        List<Grade> grades = gradeService.getGradesByStudent(studentId);
        return grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get grades for a course - GET /api/grades/course/{courseId}
    @GetMapping("/course/{courseId}")
    public List<GradeResponse> getGradesByCourse(@PathVariable Long courseId) {
        List<Grade> grades = gradeService.getGradesByCourse(courseId);
        return grades.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // calcualte GPA for a student - GET /api/grades/student/{studentId}/gpa
    @GetMapping("/student/{studentId}/gpa")
    public ResponseEntity<Double> calculateGPA(@PathVariable Long studentId) {
        Double gpa = gradeService.calculateGPA(studentId);
        return ResponseEntity.ok(gpa);
    }

    // get GPA summary for a student - GET /api/grades/student/{studentId}/summary
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<GradeService.GPASummary> getGPASummary(@PathVariable Long studentId) {
        GradeService.GPASummary summary = gradeService.getGPASummary(studentId);
        return ResponseEntity.ok(summary);
    }

    // update grade - PUT /api/grades/{id}
    @PutMapping("/{id}")
    public GradeResponse updateGrade(@PathVariable Long id, @RequestParam String gradeValue) {
        // TODO: Get current user ID from security context instead of using null
        Grade grade = gradeService.updateGrade(id, gradeValue, null, "Grade updated");
        return convertToResponse(grade);
    }

    // delete grade - DELETE /api/grades/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGrade(@PathVariable Long id) {
        gradeService.deleteGrade(id);
    }

    // get grade scale - GET /api/grades/scale
    @GetMapping("/scale")
    public ResponseEntity<Map<String, Double>> getGradeScale() {
        return ResponseEntity.ok(GradeService.getGradeScale());
    }

    // helper method to convert Grade to GradeResponse
    private GradeResponse convertToResponse(Grade grade) {
        GradeResponse.StudentInfo studentInfo = new GradeResponse.StudentInfo(
            grade.getStudent().getId(),
            grade.getStudent().getUsername(),
            grade.getStudent().getEmail()
        );

        GradeResponse.CourseInfo courseInfo = new GradeResponse.CourseInfo(
            grade.getCourse().getId(),
            grade.getCourse().getCourseCode(),
            grade.getCourse().getCourseName(),
            grade.getCourse().getCredits()
        );

        GradeResponse response = new GradeResponse();
        response.setId(grade.getId());
        response.setStudent(studentInfo);
        response.setCourse(courseInfo);
        response.setGradeValue(grade.getGradeValue());
        response.setGradePoints(grade.getGradePoints());
        response.setCreatedAt(grade.getCreatedAt());

        return response;
    }
}
