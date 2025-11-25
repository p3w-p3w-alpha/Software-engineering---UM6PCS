package com.sams.service;

import com.sams.dto.*;
import com.sams.entity.*;
import com.sams.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class FeeService {

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private FeeItemRepository feeItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SemesterRepository semesterRepository;

    @Autowired
    private DegreeProgramRepository degreeProgramRepository;

    // ============================================
    // Fee Structure Management
    // ============================================

    /**
     * Create a new fee structure
     */
    @Transactional
    public FeeStructureResponse createFeeStructure(FeeStructureRequest request) {
        // Check if fee code already exists
        if (feeStructureRepository.existsByFeeCode(request.getFeeCode())) {
            throw new IllegalArgumentException("Fee code already exists: " + request.getFeeCode());
        }

        // Check if fee name already exists
        if (feeStructureRepository.existsByFeeName(request.getFeeName())) {
            throw new IllegalArgumentException("Fee name already exists: " + request.getFeeName());
        }

        FeeStructure feeStructure = new FeeStructure();
        feeStructure.setFeeName(request.getFeeName());
        feeStructure.setFeeCode(request.getFeeCode());
        feeStructure.setCategory(request.getCategory());
        feeStructure.setDefaultAmount(request.getDefaultAmount());
        feeStructure.setDescription(request.getDescription());
        feeStructure.setMandatory(request.getMandatory());
        feeStructure.setRecurring(request.getRecurring());
        feeStructure.setActive(request.getActive());
        feeStructure.setApplicableFromSemester(request.getApplicableFromSemester());
        feeStructure.setApplicableToSemester(request.getApplicableToSemester());

        // Set program if provided
        if (request.getProgramId() != null) {
            DegreeProgram program = degreeProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
            feeStructure.setProgram(program);
        }

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return convertToResponse(saved);
    }

    /**
     * Update an existing fee structure
     */
    @Transactional
    public FeeStructureResponse updateFeeStructure(Long id, FeeStructureRequest request) {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));

        // Check if new fee code conflicts with existing
        if (!feeStructure.getFeeCode().equals(request.getFeeCode()) &&
            feeStructureRepository.existsByFeeCode(request.getFeeCode())) {
            throw new IllegalArgumentException("Fee code already exists: " + request.getFeeCode());
        }

        // Check if new fee name conflicts with existing
        if (!feeStructure.getFeeName().equals(request.getFeeName()) &&
            feeStructureRepository.existsByFeeName(request.getFeeName())) {
            throw new IllegalArgumentException("Fee name already exists: " + request.getFeeName());
        }

        feeStructure.setFeeName(request.getFeeName());
        feeStructure.setFeeCode(request.getFeeCode());
        feeStructure.setCategory(request.getCategory());
        feeStructure.setDefaultAmount(request.getDefaultAmount());
        feeStructure.setDescription(request.getDescription());
        feeStructure.setMandatory(request.getMandatory());
        feeStructure.setRecurring(request.getRecurring());
        feeStructure.setActive(request.getActive());
        feeStructure.setApplicableFromSemester(request.getApplicableFromSemester());
        feeStructure.setApplicableToSemester(request.getApplicableToSemester());

        // Update program if provided
        if (request.getProgramId() != null) {
            DegreeProgram program = degreeProgramRepository.findById(request.getProgramId())
                .orElseThrow(() -> new IllegalArgumentException("Program not found"));
            feeStructure.setProgram(program);
        } else {
            feeStructure.setProgram(null);
        }

        FeeStructure saved = feeStructureRepository.save(feeStructure);
        return convertToResponse(saved);
    }

    /**
     * Delete a fee structure
     */
    @Transactional
    public void deleteFeeStructure(Long id) {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));

        // Check if fee structure is being used
        long usageCount = feeItemRepository.countByFeeStructureId(id);
        if (usageCount > 0) {
            throw new IllegalStateException("Cannot delete fee structure that is being used in fee items");
        }

        feeStructureRepository.delete(feeStructure);
    }

    /**
     * Get fee structure by ID
     */
    public FeeStructureResponse getFeeStructureById(Long id) {
        FeeStructure feeStructure = feeStructureRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Fee structure not found"));
        return convertToResponse(feeStructure);
    }

    /**
     * Get all fee structures
     */
    public List<FeeStructureResponse> getAllFeeStructures() {
        return feeStructureRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get active fee structures
     */
    public List<FeeStructureResponse> getActiveFeeStructures() {
        return feeStructureRepository.findByActiveTrue().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee structures for a specific program
     */
    public List<FeeStructureResponse> getFeeStructuresForProgram(Long programId, Integer semester) {
        List<FeeStructure> structures;
        if (semester != null) {
            structures = feeStructureRepository.findApplicableFeeStructures(programId, semester);
        } else {
            structures = feeStructureRepository.findActiveFeeStructuresForProgram(programId);
        }
        return structures.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee structures by category
     */
    public List<FeeStructureResponse> getFeeStructuresByCategory(String category) {
        return feeStructureRepository.findByCategoryAndActive(category, true).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    // ============================================
    // Fee Item Management
    // ============================================

    /**
     * Get fee items by payment
     */
    public List<FeeItemResponse> getFeeItemsByPayment(Long paymentId) {
        return feeItemRepository.findByPaymentId(paymentId).stream()
            .map(this::convertToFeeItemResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee items by student
     */
    public List<FeeItemResponse> getFeeItemsByStudent(Long studentId) {
        return feeItemRepository.findByStudentId(studentId).stream()
            .map(this::convertToFeeItemResponse)
            .collect(Collectors.toList());
    }

    /**
     * Get fee items by student and semester
     */
    public List<FeeItemResponse> getFeeItemsByStudentAndSemester(Long studentId, Long semesterId) {
        return feeItemRepository.findByStudentIdAndSemesterId(studentId, semesterId).stream()
            .map(this::convertToFeeItemResponse)
            .collect(Collectors.toList());
    }

    /**
     * Apply discount to a fee item
     */
    @Transactional
    public FeeItemResponse applyDiscount(Long feeItemId, BigDecimal discountAmount, String reason) {
        FeeItem feeItem = feeItemRepository.findById(feeItemId)
            .orElseThrow(() -> new IllegalArgumentException("Fee item not found"));

        feeItem.applyDiscount(discountAmount, reason);
        FeeItem saved = feeItemRepository.save(feeItem);
        return convertToFeeItemResponse(saved);
    }

    /**
     * Waive a fee item
     */
    @Transactional
    public FeeItemResponse waiveFee(Long feeItemId, String reason) {
        FeeItem feeItem = feeItemRepository.findById(feeItemId)
            .orElseThrow(() -> new IllegalArgumentException("Fee item not found"));

        feeItem.waiveFee(reason);
        FeeItem saved = feeItemRepository.save(feeItem);
        return convertToFeeItemResponse(saved);
    }

    /**
     * Remove waiver from a fee item
     */
    @Transactional
    public FeeItemResponse removeWaiver(Long feeItemId) {
        FeeItem feeItem = feeItemRepository.findById(feeItemId)
            .orElseThrow(() -> new IllegalArgumentException("Fee item not found"));

        feeItem.removeWaiver();
        FeeItem saved = feeItemRepository.save(feeItem);
        return convertToFeeItemResponse(saved);
    }

    // ============================================
    // Fee Breakdown & Reports
    // ============================================

    /**
     * Get fee breakdown for a student in a semester
     */
    public FeeBreakdownResponse getFeeBreakdown(Long studentId, Long semesterId) {
        User student = userRepository.findById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new IllegalArgumentException("Semester not found"));

        FeeBreakdownResponse response = new FeeBreakdownResponse();
        response.setStudentId(studentId);
        response.setStudentName(student.getFirstName() + " " + student.getLastName());
        response.setSemesterId(semesterId);
        response.setSemesterName(semester.getName());

        // Get all fee items for this student and semester
        List<FeeItem> feeItems = feeItemRepository.findByStudentIdAndSemesterId(studentId, semesterId);
        response.setFeeItems(feeItems.stream()
            .map(this::convertToFeeItemResponse)
            .collect(Collectors.toList()));

        // Calculate totals
        BigDecimal totalAmount = feeItems.stream()
            .map(FeeItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalAmount(totalAmount);

        BigDecimal totalDiscount = feeItems.stream()
            .map(fi -> fi.getDiscount() != null ? fi.getDiscount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalDiscount(totalDiscount);

        BigDecimal totalFinalAmount = feeItems.stream()
            .map(FeeItem::getFinalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalFinalAmount(totalFinalAmount);

        // Get category breakdowns
        List<Object[]> categoryData = feeItemRepository.getFeeBreakdownByCategory(studentId, semesterId);
        List<FeeBreakdownResponse.CategoryBreakdown> categoryBreakdowns = new ArrayList<>();

        for (Object[] data : categoryData) {
            String category = (String) data[0];
            BigDecimal amount = (BigDecimal) data[1];
            BigDecimal percentage = totalFinalAmount.compareTo(BigDecimal.ZERO) > 0
                ? amount.multiply(BigDecimal.valueOf(100)).divide(totalFinalAmount, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            categoryBreakdowns.add(new FeeBreakdownResponse.CategoryBreakdown(category, amount, percentage));
        }
        response.setCategoryBreakdowns(categoryBreakdowns);

        return response;
    }

    /**
     * Get fee report for a semester
     */
    public FeeReportResponse getFeeReport(Long semesterId, String reportType) {
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new IllegalArgumentException("Semester not found"));

        FeeReportResponse report = new FeeReportResponse();
        report.setReportType(reportType);
        report.setSemesterId(semesterId);
        report.setSemesterName(semester.getName());

        // Get all payments for this semester
        List<Payment> payments = paymentRepository.findBySemesterId(semesterId);

        // Calculate totals
        BigDecimal totalFeesCollected = payments.stream()
            .filter(p -> "APPROVED".equals(p.getStatus()) || "PAID".equals(p.getStatus()))
            .map(Payment::getPaidAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalFeesCollected(totalFeesCollected);

        BigDecimal totalAmount = payments.stream()
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFeesOutstanding = totalAmount.subtract(totalFeesCollected);
        report.setTotalFeesOutstanding(totalFeesOutstanding);

        // Get total discounts and waivers
        List<FeeItem> allFeeItems = feeItemRepository.findBySemesterId(semesterId);
        BigDecimal totalDiscounts = allFeeItems.stream()
            .map(fi -> fi.getDiscount() != null ? fi.getDiscount() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalDiscountsGiven(totalDiscounts);

        BigDecimal totalWaived = allFeeItems.stream()
            .filter(FeeItem::getWaived)
            .map(FeeItem::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        report.setTotalFeesWaived(totalWaived);

        // Calculate collection rate
        if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
            double collectionRate = totalFeesCollected.multiply(BigDecimal.valueOf(100))
                .divide(totalAmount, 2, RoundingMode.HALF_UP).doubleValue();
            report.setCollectionRate(collectionRate);
        } else {
            report.setCollectionRate(0.0);
        }

        // Get student summaries
        Map<Long, FeeReportResponse.StudentFeeSummary> studentSummaryMap = new HashMap<>();
        for (Payment payment : payments) {
            Long studentId = payment.getStudent().getId();
            if (!studentSummaryMap.containsKey(studentId)) {
                FeeReportResponse.StudentFeeSummary summary = new FeeReportResponse.StudentFeeSummary();
                summary.setStudentId(studentId);
                summary.setStudentName(payment.getStudent().getFirstName() + " " + payment.getStudent().getLastName());
                summary.setProgram("N/A"); // User entity doesn't have program field
                summary.setTotalFees(BigDecimal.ZERO);
                summary.setPaidAmount(BigDecimal.ZERO);
                summary.setOutstandingAmount(BigDecimal.ZERO);
                studentSummaryMap.put(studentId, summary);
            }

            FeeReportResponse.StudentFeeSummary summary = studentSummaryMap.get(studentId);
            summary.setTotalFees(summary.getTotalFees().add(payment.getAmount()));
            summary.setPaidAmount(summary.getPaidAmount().add(payment.getPaidAmount()));
            summary.setOutstandingAmount(summary.getOutstandingAmount().add(payment.getRemainingAmount()));

            // Set status
            if (payment.isFullyPaid()) {
                summary.setStatus("PAID");
            } else if (payment.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
                summary.setStatus("PARTIAL");
            } else {
                summary.setStatus("PENDING");
            }
        }
        report.setStudentSummaries(new ArrayList<>(studentSummaryMap.values()));
        report.setTotalStudents((long) studentSummaryMap.size());
        report.setStudentsWithOutstandingFees(studentSummaryMap.values().stream()
            .filter(s -> !"PAID".equals(s.getStatus()))
            .count());

        // Get category summaries
        List<Object[]> categoryData = feeItemRepository.getFeeBreakdownByCategory(null, semesterId);
        List<FeeReportResponse.CategorySummary> categorySummaries = new ArrayList<>();
        for (Object[] data : categoryData) {
            String category = (String) data[0];
            BigDecimal amount = (BigDecimal) data[1];
            categorySummaries.add(new FeeReportResponse.CategorySummary(category, amount));
        }
        report.setCategorySummaries(categorySummaries);

        return report;
    }

    // ============================================
    // Conversion Methods
    // ============================================

    private FeeStructureResponse convertToResponse(FeeStructure feeStructure) {
        FeeStructureResponse response = new FeeStructureResponse();
        response.setId(feeStructure.getId());
        response.setFeeName(feeStructure.getFeeName());
        response.setFeeCode(feeStructure.getFeeCode());
        response.setCategory(feeStructure.getCategory());
        response.setDefaultAmount(feeStructure.getDefaultAmount());
        response.setDescription(feeStructure.getDescription());
        response.setMandatory(feeStructure.getMandatory());
        response.setRecurring(feeStructure.getRecurring());
        response.setActive(feeStructure.getActive());
        response.setApplicableFromSemester(feeStructure.getApplicableFromSemester());
        response.setApplicableToSemester(feeStructure.getApplicableToSemester());
        response.setCreatedAt(feeStructure.getCreatedAt());
        response.setUpdatedAt(feeStructure.getUpdatedAt());

        if (feeStructure.getProgram() != null) {
            response.setProgramId(feeStructure.getProgram().getId());
            response.setProgramName(feeStructure.getProgram().getName());
        }

        return response;
    }

    private FeeItemResponse convertToFeeItemResponse(FeeItem feeItem) {
        FeeItemResponse response = new FeeItemResponse();
        response.setId(feeItem.getId());
        response.setPaymentId(feeItem.getPayment().getId());
        response.setFeeStructureId(feeItem.getFeeStructure().getId());
        response.setFeeName(feeItem.getFeeStructure().getFeeName());
        response.setFeeCode(feeItem.getFeeStructure().getFeeCode());
        response.setCategory(feeItem.getFeeStructure().getCategory());
        response.setStudentId(feeItem.getStudent().getId());
        response.setStudentName(feeItem.getStudent().getFirstName() + " " + feeItem.getStudent().getLastName());
        response.setSemesterId(feeItem.getSemester().getId());
        response.setSemesterName(feeItem.getSemester().getName());
        response.setAmount(feeItem.getAmount());
        response.setDiscount(feeItem.getDiscount());
        response.setFinalAmount(feeItem.getFinalAmount());
        response.setDiscountReason(feeItem.getDiscountReason());
        response.setWaived(feeItem.getWaived());
        response.setWaiverReason(feeItem.getWaiverReason());
        response.setNotes(feeItem.getNotes());
        response.setCreatedAt(feeItem.getCreatedAt());
        return response;
    }
}
