package com.baokim.notification_service.features.email.validators.email;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class CommaSeparatedEmailValidator implements ConstraintValidator<ValidCommaSeparatedEmails, String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}(?:\\.[A-Z]{2,})?$",
            Pattern.CASE_INSENSITIVE
    );
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true;
        }

        String[] emails = value.split(",");
        for (String email : emails) {
            String trimmed = email.trim();
            if (!EMAIL_PATTERN.matcher(trimmed).matches()) {
                return false;
            }
        }
        return true;
    }
}
