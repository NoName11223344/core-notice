package com.baokim.notification_service.features.email.validators.file;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFile {
    String message() default "File không hợp lệ. Chỉ chấp nhận các định dạng: .pdf, .zip, .doc, .docx, .xls, .xlsx, .jpg, .jpeg, .png";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}