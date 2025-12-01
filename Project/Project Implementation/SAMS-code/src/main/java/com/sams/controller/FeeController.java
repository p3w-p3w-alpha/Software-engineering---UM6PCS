package com.sams.controller;

import com.sams.dto.*;
import com.sams.service.FeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * handles fee management operations - fee structures, payments, reports
 * used for managing tuition fees, course fees, and other charges
 * make sure to recalculate fees when course enrollment changes
 */
@RestController
@RequestMapping("/api/fees")
public class FeeController {

    @Autowired
    private FeeService feeService;

    // ============================================
    // Fee Structure Endpoints
    // ============================================

    /**
     * Create a new fee structure (ADMIN/SUPER_ADMIN only)
     */
    @PostMapping("/structures")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> createFeeStructure(@Valid @RequestBody FeeStructureRequest request) {
        try {
            FeeStructureResponse response = feeService.createFeeStructure(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating fee structure: " + e.getMessage());
        }
    }

    /**
     * Update an existing fee structure (ADMIN/SUPER_ADMIN only)
     */
    @PutMapping("/structures/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> updateFeeStructure(@PathVariable Long id,
                                                @Valid @RequestBody FeeStructureRequest request) {
        try {
            FeeStructureResponse response = feeService.updateFeeStructure(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating fee structure: " + e.getMessage());
        }
    }

    /**
     * Delete a fee structure (ADMIN/SUPER_ADMIN only)
     */
    @DeleteMapping("/structures/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> deleteFeeStructure(@PathVariable Long id) {
        try {
            feeService.deleteFeeStructure(id);
            return ResponseEntity.ok("Fee structure deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting fee structure: " + e.getMessage());
        }
    }

    /**
     * Get fee structure by ID
     */
    @GetMapping("/structures/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getFeeStructureById(@PathVariable Long id) {
        try {
            FeeStructureResponse response = feeService.getFeeStructureById(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee structure: " + e.getMessage());
        }
    }

    /**
     * Get all fee structures
     */
    @GetMapping("/structures")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllFeeStructures() {
        try {
            List<FeeStructureResponse> response = feeService.getAllFeeStructures();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee structures: " + e.getMessage());
        }
    }

    /**
     * Get active fee structures
     */
    @GetMapping("/structures/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getActiveFeeStructures() {
        try {
            List<FeeStructureResponse> response = feeService.getActiveFeeStructures();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching active fee structures: " + e.getMessage());
        }
    }

    /**
     * Get fee structures for a specific program
     */
    @GetMapping("/structures/program/{programId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getFeeStructuresForProgram(@PathVariable Long programId,
                                                        @RequestParam(required = false) Integer semester) {
        try {
            List<FeeStructureResponse> response = feeService.getFeeStructuresForProgram(programId, semester);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee structures for program: " + e.getMessage());
        }
    }

    /**
     * Get fee structures by category
     */
    @GetMapping("/structures/category/{category}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT') or hasRole('FACULTY')")
    public ResponseEntity<?> getFeeStructuresByCategory(@PathVariable String category) {
        try {
            List<FeeStructureResponse> response = feeService.getFeeStructuresByCategory(category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee structures by category: " + e.getMessage());
        }
    }

    // ============================================
    // Fee Item Endpoints
    // ============================================

    /**
     * Get fee items by payment ID
     */
    @GetMapping("/items/payment/{paymentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> getFeeItemsByPayment(@PathVariable Long paymentId) {
        try {
            List<FeeItemResponse> response = feeService.getFeeItemsByPayment(paymentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee items: " + e.getMessage());
        }
    }

    /**
     * Get fee items by student ID
     */
    @GetMapping("/items/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> getFeeItemsByStudent(@PathVariable Long studentId) {
        try {
            List<FeeItemResponse> response = feeService.getFeeItemsByStudent(studentId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee items: " + e.getMessage());
        }
    }

    /**
     * Get fee items by student ID and semester ID
     */
    @GetMapping("/items/student/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> getFeeItemsByStudentAndSemester(@PathVariable Long studentId,
                                                             @PathVariable Long semesterId) {
        try {
            List<FeeItemResponse> response = feeService.getFeeItemsByStudentAndSemester(studentId, semesterId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee items: " + e.getMessage());
        }
    }

    /**
     * Apply discount to a fee item (ADMIN/SUPER_ADMIN only)
     */
    @PostMapping("/items/{id}/discount")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> applyDiscount(@PathVariable Long id,
                                          @RequestParam BigDecimal amount,
                                          @RequestParam String reason) {
        try {
            FeeItemResponse response = feeService.applyDiscount(id, amount, reason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error applying discount: " + e.getMessage());
        }
    }

    /**
     * Waive a fee item (ADMIN/SUPER_ADMIN only)
     */
    @PostMapping("/items/{id}/waive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> waiveFee(@PathVariable Long id,
                                     @RequestParam String reason) {
        try {
            FeeItemResponse response = feeService.waiveFee(id, reason);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error waiving fee: " + e.getMessage());
        }
    }

    /**
     * Remove waiver from a fee item (ADMIN/SUPER_ADMIN only)
     */
    @PostMapping("/items/{id}/remove-waiver")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> removeWaiver(@PathVariable Long id) {
        try {
            FeeItemResponse response = feeService.removeWaiver(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error removing waiver: " + e.getMessage());
        }
    }

    // ============================================
    // Fee Breakdown & Reports Endpoints
    // ============================================

    /**
     * Get fee breakdown for a student in a semester
     */
    @GetMapping("/breakdown/student/{studentId}/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN') or hasRole('STUDENT')")
    public ResponseEntity<?> getFeeBreakdown(@PathVariable Long studentId,
                                             @PathVariable Long semesterId) {
        try {
            FeeBreakdownResponse response = feeService.getFeeBreakdown(studentId, semesterId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching fee breakdown: " + e.getMessage());
        }
    }

    /**
     * Get fee report for a semester
     */
    @GetMapping("/reports/semester/{semesterId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getFeeReport(@PathVariable Long semesterId,
                                         @RequestParam(defaultValue = "SUMMARY") String reportType) {
        try {
            FeeReportResponse response = feeService.getFeeReport(semesterId, reportType);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating fee report: " + e.getMessage());
        }
    }
}
