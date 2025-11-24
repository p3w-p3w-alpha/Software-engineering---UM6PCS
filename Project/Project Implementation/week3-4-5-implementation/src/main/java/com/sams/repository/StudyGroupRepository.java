package com.sams.repository;

import com.sams.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    // find all active study groups
    List<StudyGroup> findByActiveTrue();

    // find groups created by a specific user
    List<StudyGroup> findByCreatedByIdAndActiveTrue(Long userId);

    // find groups for a specific course
    List<StudyGroup> findByCourseIdAndActiveTrue(Long courseId);

    // find all public (non-private) active groups
    List<StudyGroup> findByIsPrivateFalseAndActiveTrue();

    // find all private groups
    List<StudyGroup> findByIsPrivateTrueAndActiveTrue();

    // search groups by name (case insensitive)
    List<StudyGroup> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
