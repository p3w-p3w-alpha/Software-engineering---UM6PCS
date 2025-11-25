package com.sams.repository;

import com.sams.entity.DegreeProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DegreeProgramRepository extends JpaRepository<DegreeProgram, Long> {

    // find degree program by code
    Optional<DegreeProgram> findByCode(String code);

    // find all active degree programs
    List<DegreeProgram> findByActiveTrue();

    // find degree programs by department
    List<DegreeProgram> findByDepartment(String department);

    // find degree programs by department and active status
    List<DegreeProgram> findByDepartmentAndActiveTrue(String department);
}
