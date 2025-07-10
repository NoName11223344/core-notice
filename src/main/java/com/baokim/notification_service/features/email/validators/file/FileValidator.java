package com.baokim.notification_service.features.email.validators.file;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private static final long MAX_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_TYPES = List.of(
            "application/pdf",
            "application/zip",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "image/jpeg", "image/png"
    );

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true; // Bỏ qua nếu không có file
        }
        if (file.getSize() > MAX_SIZE) return false;
        if (!ALLOWED_TYPES.contains(file.getContentType())) return false;
        return true;
    }
}

