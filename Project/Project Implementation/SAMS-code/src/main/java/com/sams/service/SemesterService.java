package com.sams.service;

import com.sams.entity.Semester;
import com.sams.exception.SemesterNotFoundException;
import com.sams.repository.SemesterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * handles all semester operations - creating, updating, activating semesters
 * this service is pretty straightforward but the activation logic was tricky
 *
 * had to make sure only one semester can be active at a time
 * also deals with registration periods and validation - might optimize later
 */
@Service
public class SemesterService {

    private final SemesterRepository semesterRepository;

    public SemesterService(SemesterRepository semesterRepository) {
        this.semesterRepository = semesterRepository;
    }

    // create new semester
    @Transactional
    public Semester createSemester(Semester semester) {
        // validate required fields
        if (semester.getName() == null || semester.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Semester name is required");
        }

        if (semester.getCode() == null || semester.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Semester code is required");
        }

        // chekc if code already exists
        if (semesterRepository.existsByCode(semester.getCode())) {
            throw new IllegalArgumentException("Semester code already exists: " + semester.getCode());
        }

        // validate dates
        if (semester.getStartDate() == null || semester.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (semester.getStartDate().isAfter(semester.getEndDate())) {
            throw new IllegalArgumentException("Start date must be before end date");
        }

        return semesterRepository.save(semester);
    }

    // get semester by id
    public Semester getSemesterById(Long id) {
        return semesterRepository.findById(id)
                .orElseThrow(() -> new SemesterNotFoundException(id));
    }

    // get semester by code
    public Semester getSemesterByCode(String code) {
        return semesterRepository.findByCode(code)
                .orElseThrow(() -> new SemesterNotFoundException("code", code));
    }

    // get all semesters
    public List<Semester> getAllSemesters() {
        return semesterRepository.findAll();
    }

    // get current active semester
    public Semester getCurrentSemester() {
        return semesterRepository.findByActiveTrue()
                .orElseThrow(() -> new SemesterNotFoundException("No active semester found"));
    }

    // update semester
    @Transactional
    public Semester updateSemester(Long id, Semester semesterDetails) {
        Semester semester = getSemesterById(id);

        // update fields
        if (semesterDetails.getName() != null) {
            semester.setName(semesterDetails.getName());
        }

        if (semesterDetails.getCode() != null && !semester.getCode().equals(semesterDetails.getCode())) {
            // chekc if new code already exists
            if (semesterRepository.existsByCode(semesterDetails.getCode())) {
                throw new IllegalArgumentException("Semester code already exists: " + semesterDetails.getCode());
            }
            semester.setCode(semesterDetails.getCode());
        }

        if (semesterDetails.getStartDate() != null) {
            semester.setStartDate(semesterDetails.getStartDate());
        }

        if (semesterDetails.getEndDate() != null) {
            semester.setEndDate(semesterDetails.getEndDate());
        }

        if (semesterDetails.getEnrollmentStartDate() != null) {
            semester.setEnrollmentStartDate(semesterDetails.getEnrollmentStartDate());
        }

        if (semesterDetails.getEnrollmentEndDate() != null) {
            semester.setEnrollmentEndDate(semesterDetails.getEnrollmentEndDate());
        }

        if (semesterDetails.getDropDeadline() != null) {
            semester.setDropDeadline(semesterDetails.getDropDeadline());
        }

        if (semesterDetails.getGradeDeadline() != null) {
            semester.setGradeDeadline(semesterDetails.getGradeDeadline());
        }

        if (semesterDetails.getRegistrationOpen() != null) {
            semester.setRegistrationOpen(semesterDetails.getRegistrationOpen());
        }

        return semesterRepository.save(semester);
    }

    // activate a semester (make it the current active semester)
    // TODO: this loops through ALL semesters which could be slow with lots of data
    // might wanna use a batch update query later instead
    @Transactional
    public Semester activateSemester(Long id) {
        Semester semester = getSemesterById(id);

        // deactivate all other semesters first
        // this part took forever to figure out - dont touch this unless you know what your doing
        List<Semester> allSemesters = semesterRepository.findAll();
        for (Semester s : allSemesters) {
            if (s.getActive()) {
                s.setActive(false);
                semesterRepository.save(s);
            }
        }

        // activate this semester
        semester.setActive(true);
        return semesterRepository.save(semester);
    }

    // open registration for a semester
    @Transactional
    public Semester openRegistration(Long id) {
        Semester semester = getSemesterById(id);
        semester.setRegistrationOpen(true);
        return semesterRepository.save(semester);
    }

    // close registration for a semester
    @Transactional
    public Semester closeRegistration(Long id) {
        Semester semester = getSemesterById(id);
        semester.setRegistrationOpen(false);
        return semesterRepository.save(semester);
    }

    // delete semester (only if no courses assigned)
    @Transactional
    public void deleteSemester(Long id) {
        Semester semester = getSemesterById(id);

        // chekc if any courses are tied to this semester
        if (semester.getCourses() != null && !semester.getCourses().isEmpty()) {
            throw new IllegalStateException("Cannot delete semester with assigned courses");
        }

        semesterRepository.delete(semester);
    }

    // search semesters by name
    public List<Semester> searchByName(String name) {
        return semesterRepository.findByNameContainingIgnoreCase(name);
    }
}
