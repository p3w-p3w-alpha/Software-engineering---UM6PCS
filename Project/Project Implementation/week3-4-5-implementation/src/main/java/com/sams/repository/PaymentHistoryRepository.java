package com.sams.repository;

import com.sams.entity.Payment;
import com.sams.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

    // find payment history for a specific payment
    List<PaymentHistory> findByPaymentOrderByCreatedAtDesc(Payment payment);

    List<PaymentHistory> findByPaymentIdOrderByCreatedAtDesc(Long paymentId);

    // find payment history by action type
    List<PaymentHistory> findByActionTypeOrderByCreatedAtDesc(String actionType);

    // find payment history by user who performed the action
    List<PaymentHistory> findByPerformedByIdOrderByCreatedAtDesc(Long userId);
}
