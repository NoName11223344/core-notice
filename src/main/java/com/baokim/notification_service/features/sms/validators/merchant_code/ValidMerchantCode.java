package com.baokim.notification_service.features.sms.validators.merchant_code;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidMerchantCodeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMerchantCode {
    String message() default "Merchant không tồn tại";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}