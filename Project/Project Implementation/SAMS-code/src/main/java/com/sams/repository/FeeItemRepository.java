package com.sams.repository;

import com.sams.entity.FeeItem;
import com.sams.entity.FeeStructure;
import com.sams.entity.Payment;
import com.sams.entity.Semester;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * repository for fee item data access
 * handles individual fee charges for students
 * needed this becuase we have complex fee calculation and discount logic
 */
@Repository
public interface FeeItemRepository extends JpaRepository<FeeItem, Long> {

    // find fee items by payment - used in payment details
    List<FeeItem> findByPayment(Payment payment);

    List<FeeItem> findByPaymentId(Long paymentId);

    // find fee items by student
    List<FeeItem> findByStudent(User student);

    List<FeeItem> findByStudentId(Long studentId);

    // find fee items by semester
    List<FeeItem> findBySemester(Semester semester);

    List<FeeItem> findBySemesterId(Long semesterId);

    // find fee items by fee structure
    List<FeeItem> findByFeeStructure(FeeStructure feeStructure);

    List<FeeItem> findByFeeStructureId(Long feeStructureId);

    // find fee items by student and semester
    List<FeeItem> findByStudentIdAndSemesterId(Long studentId, Long semesterId);

    // custom query for finding fee items by student, semester, and fee structure
    @Query("SELECT f FROM FeeItem f WHERE f.student.id = :studentId " +
           "AND f.semester.id = :semesterId " +
           "AND f.feeStructure.id = :feeStructureId")
    List<FeeItem> findByStudentSemesterAndFeeStructure(
        @Param("studentId") Long studentId,
        @Param("semesterId") Long semesterId,
        @Param("feeStructureId") Long feeStructureId
    );

    // custom query for finding waived fee items - for financial aid reporting
    @Query("SELECT f FROM FeeItem f WHERE f.waived = true")
    List<FeeItem> findWaivedFeeItems();

    // custom query for finding waived fee items by student
    @Query("SELECT f FROM FeeItem f WHERE f.student.id = :studentId AND f.waived = true")
    List<FeeItem> findWaivedFeeItemsByStudent(@Param("studentId") Long studentId);

    // custom query for finding fee items with discount
    @Query("SELECT f FROM FeeItem f WHERE f.discount > 0")
    List<FeeItem> findFeeItemsWithDiscount();

    // custom query for getting total fee amount for a student in a semester - might need to optimize
    @Query("SELECT COALESCE(SUM(f.amount), 0) FROM FeeItem f " +
           "WHERE f.student.id = :studentId AND f.semester.id = :semesterId")
    BigDecimal getTotalFeeAmount(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    // custom query for getting total final amount (after discounts/waivers) for a student in a semester
    @Query("SELECT COALESCE(SUM(f.finalAmount), 0) FROM FeeItem f " +
           "WHERE f.student.id = :studentId AND f.semester.id = :semesterId")
    BigDecimal getTotalFinalAmount(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    // custom query for getting total discount for a student in a semester
    @Query("SELECT COALESCE(SUM(f.discount), 0) FROM FeeItem f " +
           "WHERE f.student.id = :studentId AND f.semester.id = :semesterId")
    BigDecimal getTotalDiscount(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    // custom query for getting fee breakdown by category for a student in a semester - used in dashboard
    // needed this becuase JPA doesnt support GROUP BY with joins easily
    @Query("SELECT fs.category, COALESCE(SUM(f.finalAmount), 0) FROM FeeItem f " +
           "JOIN f.feeStructure fs " +
           "WHERE f.student.id = :studentId AND f.semester.id = :semesterId " +
           "GROUP BY fs.category")
    List<Object[]> getFeeBreakdownByCategory(
        @Param("studentId") Long studentId,
        @Param("semesterId") Long semesterId
    );

    // count fee items by student
    long countByStudentId(Long studentId);

    // count waived fee items by student
    @Query("SELECT COUNT(f) FROM FeeItem f WHERE f.student.id = :studentId AND f.waived = true")
    long countWaivedFeeItemsByStudent(@Param("studentId") Long studentId);

    // count fee items by fee structure
    long countByFeeStructureId(Long feeStructureId);
}
