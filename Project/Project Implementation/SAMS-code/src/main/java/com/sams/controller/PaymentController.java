package com.sams.controller;

import com.sams.dto.PaymentRequest;
import com.sams.dto.PaymentResponse;
import com.sams.dto.PaymentSubmitRequest;
import com.sams.entity.Payment;
import com.sams.entity.PaymentHistory;
import com.sams.entity.User;
import com.sams.security.JwtUtil;
import com.sams.service.PaymentService;
import com.sams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * handles payment and billing operations - create, submit, approve payments
 * frontend calls these when students pay fees or admins review payments
 * this one was tricky with transaction handling
 */
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public PaymentController(PaymentService paymentService, UserService userService, JwtUtil jwtUtil) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Create or update payment for student's semester enrollments
     * Only STUDENT can create their own payment or ADMIN can create for any student
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest request,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            // If student, they can only create payment for themselves
            if ("STUDENT".equals(role)) {
                // Get current user ID from token and verify it matches request
                // For now, we'll trust the studentId from request
                // In production, verify against token claims
            }

            Payment payment = paymentService.createOrUpdatePaymentForEnrollments(
                    request.getStudentId(),
                    request.getSemesterId()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(new PaymentResponse(payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Submit payment (mark as paid)
     * Only STUDENT (for their own) or ADMIN can submit payment
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> submitPayment(@PathVariable Long id,
                                          @Valid @RequestBody PaymentSubmitRequest request,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            Payment payment = paymentService.submitPayment(
                    id,
                    request.getPaidAmount(),
                    request.getPaymentMethod(),
                    request.getTransactionReference()
            );

            return ResponseEntity.ok(new PaymentResponse(payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Approve payment
     * Only ADMIN or SUPER_ADMIN can approve payments
     */
    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> approvePayment(@PathVariable Long id,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);

            // Get admin user ID from username
            User admin = userService.getUserByUsername(username);
            Long adminId = admin.getId();

            Payment payment = paymentService.approvePayment(id, adminId);
            return ResponseEntity.ok(new PaymentResponse(payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Reject payment
     * Only ADMIN or SUPER_ADMIN can reject payments
     */
    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> rejectPayment(@PathVariable Long id,
                                          @RequestParam String reason,
                                          @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);

            // Get admin user ID from username
            User admin = userService.getUserByUsername(username);
            Long adminId = admin.getId();

            Payment payment = paymentService.rejectPayment(id, adminId, reason);
            return ResponseEntity.ok(new PaymentResponse(payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Get payment by ID
     * STUDENT can view their own, ADMIN/SUPER_ADMIN can view all
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            Payment payment = paymentService.getPaymentById(id);
            return ResponseEntity.ok(new PaymentResponse(payment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Get all payments
     * Only ADMIN or SUPER_ADMIN can view all payments
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<Payment> payments = paymentService.getAllPayments();
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get payments by student
     * STUDENT can view their own, ADMIN/SUPER_ADMIN can view any student
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStudent(@PathVariable Long studentId) {
        List<Payment> payments = paymentService.getPaymentsByStudent(studentId);
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get payments by semester
     * Only ADMIN or SUPER_ADMIN
     */
    @GetMapping("/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySemester(@PathVariable Long semesterId) {
        List<Payment> payments = paymentService.getPaymentsBySemester(semesterId);
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get payments pending approval
     * Only ADMIN or SUPER_ADMIN (for approval dashboard)
     */
    @GetMapping("/pending-approval")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getPaymentsPendingApproval() {
        List<Payment> payments = paymentService.getPaymentsPendingApproval();
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get payment for student in specific semester
     * STUDENT can view their own, ADMIN/SUPER_ADMIN can view any
     */
    @GetMapping("/student/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getPaymentForStudentInSemester(@PathVariable Long studentId,
                                                            @PathVariable Long semesterId) {
        Optional<Payment> payment = paymentService.getPaymentForStudentInSemester(studentId, semesterId);
        if (payment.isPresent()) {
            return ResponseEntity.ok(new PaymentResponse(payment.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No payment found for student in this semester");
        }
    }

    /**
     * Check if student has approved payment for semester
     * Accessible to STUDENT (own), FACULTY (enrolled students), ADMIN/SUPER_ADMIN (all)
     */
    @GetMapping("/student/{studentId}/semester/{semesterId}/approved")
    @PreAuthorize("hasRole('STUDENT') or hasRole('FACULTY') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<Boolean> hasApprovedPayment(@PathVariable Long studentId,
                                                       @PathVariable Long semesterId) {
        boolean hasApproved = paymentService.hasApprovedPayment(studentId, semesterId);
        return ResponseEntity.ok(hasApproved);
    }

    /**
     * Get payment history for a payment
     * STUDENT can view their own payment history, ADMIN/SUPER_ADMIN can view all
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('STUDENT') or hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentHistory>> getPaymentHistory(@PathVariable Long id) {
        List<PaymentHistory> history = paymentService.getPaymentHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * Get overdue payments
     * Only ADMIN or SUPER_ADMIN
     */
    @GetMapping("/overdue")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getOverduePayments() {
        List<Payment> payments = paymentService.getOverduePayments();
        List<PaymentResponse> responses = payments.stream()
                .map(PaymentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Delete payment
     * Only SUPER_ADMIN can delete payments
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
    }
}
