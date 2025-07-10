package com.baokim.notification_service.features.sms.validators.reference_id_partner;

import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueReferenceIdValidator implements ConstraintValidator<UniqueReferenceId, String> {

    private final SmsTransactionRepository smsTransactionRepository;

    @Override
    public boolean isValid(String referenceIdPartner, ConstraintValidatorContext context) {
        return referenceIdPartner != null && !smsTransactionRepository.existsByReferenceIdPartner(referenceIdPartner);
    }
}
