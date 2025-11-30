package com.sams.service;

import com.sams.entity.*;
import com.sams.exception.PaymentNotFoundException;
import com.sams.repository.EnrollmentRepository;
import com.sams.repository.PaymentHistoryRepository;
import com.sams.repository.PaymentRepository;
import com.sams.repository.SemesterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing payments and billing
 * Handles payment creation, submission, approval, and history tracking
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final SemesterRepository semesterRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public PaymentService(PaymentRepository paymentRepository,
                         PaymentHistoryRepository paymentHistoryRepository,
                         EnrollmentRepository enrollmentRepository,
                         SemesterRepository semesterRepository,
                         UserService userService,
                         NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.paymentHistoryRepository = paymentHistoryRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.semesterRepository = semesterRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Create or update payment for a student's semester enrollments
     * Calculates total based on course fees
     */
    @Transactional
    public Payment createOrUpdatePaymentForEnrollments(Long studentId, Long semesterId) {
        User student = userService.getUserById(studentId);
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found with id: " + semesterId));

        // validate student role
        if (!"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Only students can have payments");
        }

        // get all pending enrollments for this student in this semester
        List<Enrollment> enrollments = enrollmentRepository.findByStudentIdAndStatus(studentId, "PENDING_PAYMENT");

        // filter enrollments for this semester only
        enrollments = enrollments.stream()
                .filter(e -> e.getCourse().getSemester() != null &&
                           e.getCourse().getSemester().getId().equals(semesterId))
                .toList();

        if (enrollments.isEmpty()) {
            throw new IllegalArgumentException("No pending enrollments found for this semester");
        }

        // calculate total amount based on course fees
        BigDecimal totalAmount = enrollments.stream()
                .map(e -> e.getCourse().getCourseFee() != null ?
                         e.getCourse().getCourseFee() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // check if payment already exists for this student and semester
        Optional<Payment> existingPayment = paymentRepository.findByStudentAndSemester(student, semester);

        Payment payment;
        if (existingPayment.isPresent()) {
            // update existing payment
            payment = existingPayment.get();
            BigDecimal previousAmount = payment.getAmount();
            payment.setAmount(totalAmount);
            payment.setUpdatedAt(LocalDateTime.now());

            // create history entry
            createPaymentHistory(payment, "UPDATED", student,
                    String.format("Payment amount updated from %.2f to %.2f",
                            previousAmount, totalAmount));
        } else {
            // create new payment
            payment = new Payment(student, semester, totalAmount);

            // set due date (e.g., 7 days from now)
            payment.setDueDate(LocalDateTime.now().plusDays(7));

            payment = paymentRepository.save(payment);

            // create history entry
            createPaymentHistory(payment, "CREATED", student,
                    String.format("Payment created for %.2f", totalAmount));
        }

        // associate enrollments with this payment
        for (Enrollment enrollment : enrollments) {
            enrollment.setPayment(payment);
            enrollmentRepository.save(enrollment);
        }

        return paymentRepository.save(payment);
    }

    /**
     * Submit payment (student action)
     */
    @Transactional
    public Payment submitPayment(Long paymentId, BigDecimal paidAmount,
                                 String paymentMethod, String transactionReference) {
        Payment payment = getPaymentById(paymentId);

        if (payment.isApproved()) {
            throw new IllegalStateException("Payment is already approved");
        }

        if (paidAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Paid amount must be greater than zero");
        }

        String previousStatus = payment.getStatus();
        BigDecimal previousPaidAmount = payment.getPaidAmount();

        // update payment
        payment.markAsPaid(paidAmount, paymentMethod, transactionReference);
        payment = paymentRepository.save(payment);

        // create history entry
        createPaymentHistory(payment, "PAID", payment.getStudent(),
                String.format("Payment submitted: %.2f via %s (Ref: %s). Status changed from %s to %s",
                        paidAmount, paymentMethod, transactionReference, previousStatus, payment.getStatus()));

        // send notification to admins
        notificationService.notifyAdminsOfNewPayment(payment);

        // send confirmation notification to student
        notificationService.createNotification(
                payment.getStudent(),
                "PAYMENT",
                "Payment Submitted",
                String.format("Your payment of %.2f for %s has been submitted for approval",
                        paidAmount, payment.getSemester().getName()),
                null,
                "Payment",
                payment.getId()
        );

        return payment;
    }

    /**
     * Approve payment (admin action)
     */
    @Transactional
    public Payment approvePayment(Long paymentId, Long adminId) {
        Payment payment = getPaymentById(paymentId);
        User admin = userService.getUserById(adminId);

        // validate admin role
        if (!admin.getRole().contains("ADMIN")) {
            throw new IllegalArgumentException("Only admins can approve payments");
        }

        if (!payment.isFullyPaid()) {
            throw new IllegalStateException("Payment must be fully paid before approval");
        }

        if (payment.isApproved()) {
            throw new IllegalStateException("Payment is already approved");
        }

        String previousStatus = payment.getStatus();

        // approve payment
        payment.approve(admin);
        payment = paymentRepository.save(payment);

        // activate all enrollments associated with this payment
        List<Enrollment> enrollments = enrollmentRepository.findByPaymentId(payment.getId());
        for (Enrollment enrollment : enrollments) {
            if ("PENDING_PAYMENT".equals(enrollment.getStatus())) {
                enrollment.setStatus("ACTIVE");
                enrollmentRepository.save(enrollment);
            }
        }

        // create history entry
        createPaymentHistory(payment, "APPROVED", admin,
                String.format("Payment approved by %s. Status changed from %s to APPROVED. %d enrollments activated.",
                        admin.getUsername(), previousStatus, enrollments.size()));

        // send notification to student
        notificationService.createNotification(
                payment.getStudent(),
                "PAYMENT",
                "Payment Approved",
                String.format("Your payment for %s has been approved. Your enrollments are now active!",
                        payment.getSemester().getName()),
                null,
                "Payment",
                payment.getId()
        );

        return payment;
    }

    /**
     * Reject payment (admin action)
     */
    @Transactional
    public Payment rejectPayment(Long paymentId, Long adminId, String reason) {
        Payment payment = getPaymentById(paymentId);
        User admin = userService.getUserById(adminId);

        // validate admin role
        if (!admin.getRole().contains("ADMIN")) {
            throw new IllegalArgumentException("Only admins can reject payments");
        }

        if (payment.isApproved()) {
            throw new IllegalStateException("Cannot reject an approved payment");
        }

        String previousStatus = payment.getStatus();

        // reject payment
        payment.reject(admin, reason);
        payment = paymentRepository.save(payment);

        // create history entry
        createPaymentHistory(payment, "REJECTED", admin,
                String.format("Payment rejected by %s. Reason: %s. Status changed from %s to REJECTED.",
                        admin.getUsername(), reason, previousStatus));

        // send notification to student
        notificationService.createNotification(
                payment.getStudent(),
                "PAYMENT",
                "Payment Rejected",
                String.format("Your payment for %s has been rejected. Reason: %s",
                        payment.getSemester().getName(), reason),
                null,
                "Payment",
                payment.getId()
        );

        return payment;
    }

    /**
     * Get payment by ID
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
    }

    /**
     * Get all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payments by student
     */
    public List<Payment> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    /**
     * Get payments by semester
     */
    public List<Payment> getPaymentsBySemester(Long semesterId) {
        return paymentRepository.findBySemesterId(semesterId);
    }

    /**
     * Get payments pending approval (for admin dashboard)
     */
    public List<Payment> getPaymentsPendingApproval() {
        return paymentRepository.findPaymentsPendingApproval();
    }

    /**
     * Get payment for student in specific semester
     */
    public Optional<Payment> getPaymentForStudentInSemester(Long studentId, Long semesterId) {
        return paymentRepository.findByStudentIdAndSemesterId(studentId, semesterId);
    }

    /**
     * Check if student has approved payment for semester
     */
    public boolean hasApprovedPayment(Long studentId, Long semesterId) {
        return paymentRepository.hasApprovedPayment(studentId, semesterId);
    }

    /**
     * Get payment history for a payment
     */
    public List<PaymentHistory> getPaymentHistory(Long paymentId) {
        return paymentHistoryRepository.findByPaymentIdOrderByCreatedAtDesc(paymentId);
    }

    /**
     * Get overdue payments
     */
    public List<Payment> getOverduePayments() {
        return paymentRepository.findOverduePayments(LocalDateTime.now());
    }

    /**
     * Create payment history entry (internal helper)
     */
    private void createPaymentHistory(Payment payment, String actionType, User performedBy, String description) {
        createPaymentHistory(payment, actionType, performedBy, description, null, null);
    }

    private void createPaymentHistory(Payment payment, String actionType, User performedBy, String description,
                                      BigDecimal previousAmount, String previousStatus) {
        PaymentHistory history = new PaymentHistory();
        history.setPayment(payment);
        history.setActionType(actionType);
        history.setPerformedBy(performedBy);
        history.setDescription(description);
        history.setPreviousStatus(previousStatus != null ? previousStatus : payment.getStatus());
        history.setNewStatus(payment.getStatus());
        // Fix: When previousAmount is null for new actions, use BigDecimal.ZERO instead of current amount
        // This correctly represents that there was no previous amount before this action
        history.setPreviousAmount(previousAmount != null ? previousAmount : BigDecimal.ZERO);
        history.setNewAmount(payment.getPaidAmount() != null ? payment.getPaidAmount() : BigDecimal.ZERO);

        paymentHistoryRepository.save(history);
    }

    /**
     * Delete payment (soft delete - for admin only)
     */
    @Transactional
    public void deletePayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);

        if (payment.isApproved()) {
            throw new IllegalStateException("Cannot delete an approved payment");
        }

        // remove payment association from enrollments
        List<Enrollment> enrollments = enrollmentRepository.findByPaymentId(paymentId);
        for (Enrollment enrollment : enrollments) {
            enrollment.setPayment(null);
            enrollmentRepository.save(enrollment);
        }

        paymentRepository.delete(payment);
    }
}
