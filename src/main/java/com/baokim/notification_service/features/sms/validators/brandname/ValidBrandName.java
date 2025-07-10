package com.baokim.notification_service.features.sms.validators.brandname;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBrandNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBrandName {
    String message() default "Brandname không tồn tại";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
