package com.baokim.notification_service.features.sms.repositories.sms;

import com.baokim.notification_service.features.sms.entities.sms.SmsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;


public interface SmsTransactionRepository extends JpaRepository<SmsTransaction, String> {
    Optional<SmsTransaction> findByTransId(String transId);

    boolean existsByReferenceIdPartner(String referenceId);

    @Query("SELECT COUNT(s) FROM SmsTransaction s " +
            "WHERE s.status = :status AND s.merchantCode = :merchantCode " +
            "AND s.createdAt BETWEEN :startOfDay AND :now")
    Integer countTransactionsTodayByMerchantAndStatus(@Param("merchantCode") String merchantCode,
                                              @Param("status") int status,
                                              @Param("startOfDay") LocalDateTime startOfDay,
                                              @Param("now") LocalDateTime now);

}
