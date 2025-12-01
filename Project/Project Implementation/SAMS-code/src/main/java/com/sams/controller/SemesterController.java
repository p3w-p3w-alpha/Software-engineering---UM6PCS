package com.sams.controller;

import com.sams.dto.SemesterRequest;
import com.sams.dto.SemesterResponse;
import com.sams.entity.Semester;
import com.sams.service.SemesterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * handles semester management - CRUD, activate, registration periods
 * frontend uses this to show current semester and registration windows
 * only one semester can be active at a time
 */
@RestController
@RequestMapping("/api/semesters")
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    // create new semester (admin only)
    @PostMapping
    public ResponseEntity<SemesterResponse> createSemester(@Valid @RequestBody SemesterRequest request) {
        Semester semester = convertToEntity(request);
        Semester created = semesterService.createSemester(semester);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(created));
    }

    // get all semesters
    @GetMapping
    public ResponseEntity<List<SemesterResponse>> getAllSemesters() {
        List<Semester> semesters = semesterService.getAllSemesters();
        List<SemesterResponse> responses = semesters.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // get semester by id
    @GetMapping("/{id}")
    public ResponseEntity<SemesterResponse> getSemesterById(@PathVariable Long id) {
        Semester semester = semesterService.getSemesterById(id);
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // get semester by code
    @GetMapping("/code/{code}")
    public ResponseEntity<SemesterResponse> getSemesterByCode(@PathVariable String code) {
        Semester semester = semesterService.getSemesterByCode(code);
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // get current active semester
    @GetMapping("/current")
    public ResponseEntity<SemesterResponse> getCurrentSemester() {
        Semester semester = semesterService.getCurrentSemester();
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // update semester (admin only)
    @PutMapping("/{id}")
    public ResponseEntity<SemesterResponse> updateSemester(
            @PathVariable Long id,
            @Valid @RequestBody SemesterRequest request) {
        Semester semesterDetails = convertToEntity(request);
        Semester updated = semesterService.updateSemester(id, semesterDetails);
        return ResponseEntity.ok(convertToResponse(updated));
    }

    // activate semester (make it the current active semester) (admin only)
    @PostMapping("/{id}/activate")
    public ResponseEntity<SemesterResponse> activateSemester(@PathVariable Long id) {
        Semester semester = semesterService.activateSemester(id);
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // open registration for semester (admin only)
    @PostMapping("/{id}/open-registration")
    public ResponseEntity<SemesterResponse> openRegistration(@PathVariable Long id) {
        Semester semester = semesterService.openRegistration(id);
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // close registration for semester (admin only)
    @PostMapping("/{id}/close-registration")
    public ResponseEntity<SemesterResponse> closeRegistration(@PathVariable Long id) {
        Semester semester = semesterService.closeRegistration(id);
        return ResponseEntity.ok(convertToResponse(semester));
    }

    // delete semester (admin only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSemester(@PathVariable Long id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.noContent().build();
    }

    // search semesters by name
    @GetMapping("/search")
    public ResponseEntity<List<SemesterResponse>> searchSemesters(@RequestParam String name) {
        List<Semester> semesters = semesterService.searchByName(name);
        List<SemesterResponse> responses = semesters.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // helper method to convert entity to response dto
    private SemesterResponse convertToResponse(Semester semester) {
        SemesterResponse response = new SemesterResponse();
        response.setId(semester.getId());
        response.setName(semester.getName());
        response.setCode(semester.getCode());
        response.setStartDate(semester.getStartDate());
        response.setEndDate(semester.getEndDate());
        response.setEnrollmentStartDate(semester.getEnrollmentStartDate());
        response.setEnrollmentEndDate(semester.getEnrollmentEndDate());
        response.setDropDeadline(semester.getDropDeadline());
        response.setGradeDeadline(semester.getGradeDeadline());
        response.setActive(semester.getActive());
        response.setRegistrationOpen(semester.getRegistrationOpen());
        response.setCreatedAt(semester.getCreatedAt());
        response.setUpdatedAt(semester.getUpdatedAt());
        return response;
    }

    // helper method to convert request dto to entity
    private Semester convertToEntity(SemesterRequest request) {
        Semester semester = new Semester();
        semester.setName(request.getName());
        semester.setCode(request.getCode());
        semester.setStartDate(request.getStartDate());
        semester.setEndDate(request.getEndDate());
        semester.setEnrollmentStartDate(request.getEnrollmentStartDate());
        semester.setEnrollmentEndDate(request.getEnrollmentEndDate());
        semester.setDropDeadline(request.getDropDeadline());
        semester.setGradeDeadline(request.getGradeDeadline());

        if (request.getRegistrationOpen() != null) {
            semester.setRegistrationOpen(request.getRegistrationOpen());
        }

        return semester;
    }
}
