package com.baokim.notification_service.features.sms.validators.brandname;

import com.baokim.notification_service.features.sms.constants.BrandNameConstant;
import com.baokim.notification_service.features.sms.repositories.brandname.BrandNameRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidBrandNameValidator implements ConstraintValidator<ValidBrandName, String> {

    private final BrandNameRepository brandNameRepository;

    @Override
    public boolean isValid(String brandName, ConstraintValidatorContext context) {
        return brandName != null && brandNameRepository.existsByBrandNameAndStatus(brandName, BrandNameConstant.STATUS_ACTIVE);
    }
}
