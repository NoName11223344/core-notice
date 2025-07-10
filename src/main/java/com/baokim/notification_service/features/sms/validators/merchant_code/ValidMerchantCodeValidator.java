package com.baokim.notification_service.features.sms.validators.merchant_code;

import com.baokim.notification_service.features.sms.repositories.merchant.MerchantRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidMerchantCodeValidator implements ConstraintValidator<ValidMerchantCode, String> {

    private final MerchantRepository merchantRepository;

    @Override
    public boolean isValid(String merchantCode, ConstraintValidatorContext context) {
        return merchantCode != null && merchantRepository.existsByMerchantCode(merchantCode);
    }
}