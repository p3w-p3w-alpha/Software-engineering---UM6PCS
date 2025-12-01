package com.sams.repository;

import com.sams.entity.DegreeProgram;
import com.sams.entity.DegreeRequirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * repository for degree requirement data access
 * handles course requirements for degree programs
 */
@Repository
public interface DegreeRequirementRepository extends JpaRepository<DegreeRequirement, Long> {

    // find all requirements for a degree program - used in degree audit
    List<DegreeRequirement> findByDegreeProgram(DegreeProgram degreeProgram);

    List<DegreeRequirement> findByDegreeProgramId(Long degreeProgramId);

    // find requirements by type
    List<DegreeRequirement> findByDegreeProgramIdAndType(Long degreeProgramId, String type);

    // find mandatory requirements for a program
    List<DegreeRequirement> findByDegreeProgramIdAndMandatoryTrue(Long degreeProgramId);

    // find requirements ordered by display order
    List<DegreeRequirement> findByDegreeProgramIdOrderByDisplayOrderAsc(Long degreeProgramId);

    // find active requirements
    List<DegreeRequirement> findByDegreeProgramIdAndActiveTrue(Long degreeProgramId);
}
