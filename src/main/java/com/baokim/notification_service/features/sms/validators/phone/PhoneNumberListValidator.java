package com.baokim.notification_service.features.sms.validators.phone;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

public class PhoneNumberListValidator implements ConstraintValidator<ValidPhoneNumbers, String> {

    private static final String PHONE_REGEX = "^(0[93578]|84[93578])[0-9]{8}$";
    private static final Pattern PATTERN = Pattern.compile(PHONE_REGEX);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return false;
        }

        return Arrays.stream(value.split(","))
                .map(String::trim)
                .allMatch(phone -> PATTERN.matcher(phone).matches());
    }
}
