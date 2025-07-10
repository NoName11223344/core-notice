package com.baokim.notification_service.features.sms.validators.request_id;

import com.baokim.notification_service.features.sms.repositories.request_log.RequestLogRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueRequestIdValidator implements ConstraintValidator<UniqueRequestId, String> {

    private final RequestLogRepository requestLogRepository;

    @Override
    public boolean isValid(String requestId, ConstraintValidatorContext context) {
        return requestId != null && !requestLogRepository.existsByRequestId(requestId);
    }
}