package com.sams.repository;

import com.sams.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {

    // find semester by code
    Optional<Semester> findByCode(String code);

    // check if code exists
    boolean existsByCode(String code);

    // get the current active semester
    Optional<Semester> findByActiveTrue();

    // get all semesters with registration open
    List<Semester> findByRegistrationOpenTrue();

    // find semesters by name containing (for search)
    List<Semester> findByNameContainingIgnoreCase(String name);
}
