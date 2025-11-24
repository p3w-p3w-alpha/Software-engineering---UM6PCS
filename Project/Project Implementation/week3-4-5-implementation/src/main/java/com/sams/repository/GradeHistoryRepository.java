package com.sams.repository;

import com.sams.entity.Grade;
import com.sams.entity.GradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {

    // find all history entries for a specific grade
    List<GradeHistory> findByGradeOrderByModifiedAtDesc(Grade grade);

    // find all history entries for a grade by grade id
    List<GradeHistory> findByGradeIdOrderByModifiedAtDesc(Long gradeId);

    // find all modifications by a specific user (for admin oversight)
    List<GradeHistory> findByModifiedByIdOrderByModifiedAtDesc(Long userId);

    // find all modifications of a specific action type
    List<GradeHistory> findByActionTypeOrderByModifiedAtDesc(String actionType);
}
