package com.sams.repository;

import com.sams.entity.DegreeProgram;
import com.sams.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeeStructureRepository extends JpaRepository<FeeStructure, Long> {

    // find by fee code
    Optional<FeeStructure> findByFeeCode(String feeCode);

    // find by fee name
    Optional<FeeStructure> findByFeeName(String feeName);

    // find by category
    List<FeeStructure> findByCategory(String category);

    // find active fee structures
    List<FeeStructure> findByActiveTrue();

    // find mandatory fee structures
    List<FeeStructure> findByMandatoryTrue();

    // find recurring fee structures
    List<FeeStructure> findByRecurringTrue();

    // find fee structures by program
    List<FeeStructure> findByProgram(DegreeProgram program);

    List<FeeStructure> findByProgramId(Long programId);

    // find fee structures applicable to all programs (program is null)
    @Query("SELECT f FROM FeeStructure f WHERE f.program IS NULL AND f.active = true")
    List<FeeStructure> findGlobalFeeStructures();

    // find active fee structures for a program (includes global and program-specific)
    @Query("SELECT f FROM FeeStructure f WHERE f.active = true " +
           "AND (f.program IS NULL OR f.program.id = :programId)")
    List<FeeStructure> findActiveFeeStructuresForProgram(@Param("programId") Long programId);

    // find applicable fee structures for a student's program and semester
    @Query("SELECT f FROM FeeStructure f WHERE f.active = true " +
           "AND (f.program IS NULL OR f.program.id = :programId) " +
           "AND (f.applicableFromSemester IS NULL OR f.applicableFromSemester <= :semester) " +
           "AND (f.applicableToSemester IS NULL OR f.applicableToSemester >= :semester)")
    List<FeeStructure> findApplicableFeeStructures(
        @Param("programId") Long programId,
        @Param("semester") Integer semester
    );

    // find mandatory fee structures for a program
    @Query("SELECT f FROM FeeStructure f WHERE f.active = true AND f.mandatory = true " +
           "AND (f.program IS NULL OR f.program.id = :programId)")
    List<FeeStructure> findMandatoryFeeStructuresForProgram(@Param("programId") Long programId);

    // find fee structures by category and active status
    List<FeeStructure> findByCategoryAndActive(String category, Boolean active);

    // check if fee code exists
    boolean existsByFeeCode(String feeCode);

    // check if fee name exists
    boolean existsByFeeName(String feeName);

    // count active fee structures
    long countByActiveTrue();

    // count fee structures by category
    long countByCategory(String category);
}
