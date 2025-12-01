package com.sams.repository;

import com.sams.entity.DegreeProgram;
import com.sams.entity.StudentDegreeProgress;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * repository for student degree progress data access
 * tracks student progress towards degree completion
 * needed this becuase we need custom queries for graduation eligibility
 */
@Repository
public interface StudentDegreeProgressRepository extends JpaRepository<StudentDegreeProgress, Long> {

    // find student's degree progress - used in student dashboard
    Optional<StudentDegreeProgress> findByStudent(User student);

    Optional<StudentDegreeProgress> findByStudentId(Long studentId);

    // find all students in a degree program
    List<StudentDegreeProgress> findByDegreeProgram(DegreeProgram degreeProgram);

    List<StudentDegreeProgress> findByDegreeProgramId(Long degreeProgramId);

    // find students by status
    List<StudentDegreeProgress> findByStatus(String status);

    // find students by degree program and status
    List<StudentDegreeProgress> findByDegreeProgramIdAndStatus(Long degreeProgramId, String status);

    // custom query for finding students eligible for graduation (met requirements)
    // needed this becuase JPA doesnt support complex multi-condition queries well
    @Query("SELECT sdp FROM StudentDegreeProgress sdp WHERE " +
           "sdp.creditsCompleted >= sdp.degreeProgram.totalCreditsRequired AND " +
           "sdp.currentGpa >= sdp.degreeProgram.minimumGpa AND " +
           "sdp.status = 'ACTIVE'")
    List<StudentDegreeProgress> findEligibleForGraduation();

    // custom query for finding students at risk (low GPA or behind schedule) - for advisor alerts
    @Query("SELECT sdp FROM StudentDegreeProgress sdp WHERE " +
           "sdp.status = 'ACTIVE' AND " +
           "(sdp.currentGpa < sdp.degreeProgram.minimumGpa OR sdp.onTrack = false)")
    List<StudentDegreeProgress> findStudentsAtRisk();

    // check if student is enrolled in a degree program
    boolean existsByStudentId(Long studentId);
}
