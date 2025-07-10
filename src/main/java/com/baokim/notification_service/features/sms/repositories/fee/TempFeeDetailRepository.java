package com.baokim.notification_service.features.sms.repositories.fee;

import com.baokim.notification_service.features.sms.entities.fee.TemFeeDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TempFeeDetailRepository extends JpaRepository<TemFeeDetail, String> {
    List<TemFeeDetail> findAllByTempCodeAndStatus(String tempCode, Byte status);
}
