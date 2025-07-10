package com.baokim.notification_service.features.sms.validators.scheduled;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class ScheduledValidator implements ConstraintValidator<ValidScheduled, String> {

    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // nếu gửi lên "" cũng chấp nhận
        if (value.isEmpty()) {
            return true;
        }

        if (!value.matches(DATE_PATTERN)) {
            return false;
        }

        try {
            // Kiểm tra nếu thời gian trong `scheduled` nhỏ hơn thời gian hiện tại
            Date scheduledDate = dateFormat.parse(value);
            Date currentDate = new Date();
            return !scheduledDate.before(currentDate);
        } catch (Exception e) {
            return false;
        }
    }
}