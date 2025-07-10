package com.baokim.notification_service.features.sms.repositories.sms;

import com.baokim.notification_service.features.sms.entities.sms.SmsTransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SmsTransactionDetailRepository extends JpaRepository<SmsTransactionDetail, String> {
    Optional<SmsTransactionDetail> findByTransId(String transId);

    Optional<SmsTransactionDetail> findFirstByTransIdAndSentTo(String transId, String sentTo);

    Optional<SmsTransactionDetail> findBySubReferenceId(String subReferenceId);

}
