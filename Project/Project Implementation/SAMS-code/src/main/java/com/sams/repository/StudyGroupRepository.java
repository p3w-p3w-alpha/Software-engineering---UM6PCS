package com.sams.repository;

import com.sams.entity.StudyGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * repository for study group data access
 * handles student collaboration groups
 */
@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {

    // find all active study groups - used in group discovery
    List<StudyGroup> findByActiveTrue();

    // find groups created by a specific user - for user's group management
    List<StudyGroup> findByCreatedByIdAndActiveTrue(Long userId);

    // find groups for a specific course - used in course page
    List<StudyGroup> findByCourseIdAndActiveTrue(Long courseId);

    // find all public (non-private) active groups - for general browsing
    List<StudyGroup> findByIsPrivateFalseAndActiveTrue();

    // find all private groups - might need to optimize this query
    List<StudyGroup> findByIsPrivateTrueAndActiveTrue();

    // search groups by name (case insensitive) - for search functionality
    List<StudyGroup> findByNameContainingIgnoreCaseAndActiveTrue(String name);
}
