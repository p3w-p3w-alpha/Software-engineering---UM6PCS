package com.sams.repository;

import com.sams.entity.Payment;
import com.sams.entity.Semester;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * repository for payment data access
 * handles student fee payments and tracking
 * needed this becuase we have complex payment status workflows
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // find payments by student - used in payment history
    List<Payment> findByStudent(User student);

    List<Payment> findByStudentId(Long studentId);

    // find payments by semester
    List<Payment> findBySemester(Semester semester);

    List<Payment> findBySemesterId(Long semesterId);

    // find payments by status
    List<Payment> findByStatus(String status);

    // count payments by status (for dashboard stats without loading all)
    long countByStatus(String status);

    // find payments by student and semester
    Optional<Payment> findByStudentAndSemester(User student, Semester semester);

    Optional<Payment> findByStudentIdAndSemesterId(Long studentId, Long semesterId);

    // custom query for finding pending payments - for dashboard alerts
    @Query("SELECT p FROM Payment p WHERE p.status = 'PENDING' OR p.status = 'PARTIAL'")
    List<Payment> findPendingPayments();

    // custom query for finding payments pending approval (paid but not yet approved)
    @Query("SELECT p FROM Payment p WHERE p.status = 'PAID' ORDER BY p.paymentDate DESC")
    List<Payment> findPaymentsPendingApproval();

    // custom query for finding approved payments
    @Query("SELECT p FROM Payment p WHERE p.status = 'APPROVED'")
    List<Payment> findApprovedPayments();

    // custom query for finding overdue payments - might need to optimize performace
    @Query("SELECT p FROM Payment p WHERE p.dueDate < :now AND p.status NOT IN ('APPROVED', 'REJECTED')")
    List<Payment> findOverduePayments(@Param("now") LocalDateTime now);

    // find payments by student and status
    List<Payment> findByStudentIdAndStatus(Long studentId, String status);

    // check if student has approved payment for semester
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.student.id = :studentId " +
           "AND p.semester.id = :semesterId AND p.status = 'APPROVED'")
    boolean hasApprovedPayment(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    // get total payment amount for student in semester
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.student.id = :studentId AND p.semester.id = :semesterId")
    java.math.BigDecimal getTotalPaymentAmount(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);

    // get total paid amount for student in semester
    @Query("SELECT COALESCE(SUM(p.paidAmount), 0) FROM Payment p " +
           "WHERE p.student.id = :studentId AND p.semester.id = :semesterId")
    java.math.BigDecimal getTotalPaidAmount(@Param("studentId") Long studentId, @Param("semesterId") Long semesterId);
}
