package com.sams.repository;

import com.sams.entity.Grade;
import com.sams.entity.GradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * repository for grade history tracking
 * stores audit trail of all grade modifications
 * needed this becuase we need to track who changed what and when
 */
@Repository
public interface GradeHistoryRepository extends JpaRepository<GradeHistory, Long> {

    // find all history entries for a specific grade - used in audit view
    List<GradeHistory> findByGradeOrderByModifiedAtDesc(Grade grade);

    // find all history entries for a grade by grade id - alternative method for performace
    List<GradeHistory> findByGradeIdOrderByModifiedAtDesc(Long gradeId);

    // find all modifications by a specific user - for admin oversight and accountability
    List<GradeHistory> findByModifiedByIdOrderByModifiedAtDesc(Long userId);

    // find all modifications of a specific action type - useful for filtering changes
    List<GradeHistory> findByActionTypeOrderByModifiedAtDesc(String actionType);
}
