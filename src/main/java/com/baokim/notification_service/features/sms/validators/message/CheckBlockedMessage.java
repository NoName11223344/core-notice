package com.baokim.notification_service.features.sms.validators.message;

import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.features.sms.validators.merchant_code.ValidMerchantCodeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = BlockedMessageValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckBlockedMessage {
    String message() default ErrorCode.MESS_BLOCKED_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
