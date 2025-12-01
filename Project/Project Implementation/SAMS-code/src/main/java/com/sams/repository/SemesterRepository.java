package com.sams.repository;

import com.sams.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * repository for semester data access
 * handles academic semester/term information
 * might need to optimize teh active semester query for performace
 */
@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // find semester by code - used in semester lookup
    Optional<Semester> findByCode(String code);

    // check if code exists - for validation
    boolean existsByCode(String code);

    // get teh current active semester - important for enrollment
    Optional<Semester> findByActiveTrue();

    // get all semesters with registration open - used in student dashboard
    List<Semester> findByRegistrationOpenTrue();

    // find semesters by name containing - for search functionality
    List<Semester> findByNameContainingIgnoreCase(String name);
}
