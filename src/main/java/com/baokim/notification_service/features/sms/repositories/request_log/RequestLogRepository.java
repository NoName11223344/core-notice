package com.baokim.notification_service.features.sms.repositories.request_log;

import com.baokim.notification_service.features.sms.entities.request_log.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, String> {
    boolean existsByRequestId(String requestId);
}
