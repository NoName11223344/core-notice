package com.baokim.notification_service.features.sms.validators.scheduled;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ScheduledValidator.class)  // Sử dụng validator ở dưới
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidScheduled {
    String message() default "Định dạng lịch gửi tin không hợp lệ hoặc thời gian không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}