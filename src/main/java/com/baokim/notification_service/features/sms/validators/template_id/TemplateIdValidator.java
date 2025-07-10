package com.baokim.notification_service.features.sms.validators.template_id;

import com.baokim.notification_service.features.sms.constants.BrandNameConstant;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.entities.template_message.TemplateMessage;
import com.baokim.notification_service.features.sms.repositories.template_message.TemplateMessageRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
@RequiredArgsConstructor
public class TemplateIdValidator implements ConstraintValidator<ValidTemplateId, BaseSmsSendRequest> {

    private final TemplateMessageRepository templateMessageRepository;

    public boolean isValid(BaseSmsSendRequest request, ConstraintValidatorContext constraintValidatorContext) {
        if (request == null) {
            return true;
        }

        Long templateId = request.getTemplateId();

        if (templateId == null) {
            return true;
        }

        TemplateMessage templateMessage = templateMessageRepository
                .findByIdAndMerchantCodeAndBrandNameAndStatus(
                        templateId,
                        request.getMerchantCode(),
                        request.getFrom(),
                        BrandNameConstant.STATUS_ACTIVE
                )
                .orElse(null);

        if (templateMessage == null) {
            log.info("Không tìm thấy Template message id: {}", templateId);
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Không tìm thấy template message id")
                    .addConstraintViolation();
            return false;
        }

        if (templateMessage.getRegexPattern() == null) {
            log.info("Template message id {} không có regex pattern hợp lệ.", templateId);
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Template message id không có regex pattern hợp lệ.")
                    .addConstraintViolation();
            return false;
        }

        try {
            Pattern pattern = Pattern.compile(templateMessage.getRegexPattern());
            Matcher matcher = pattern.matcher(request.getMessage());

            if (!matcher.find()) {
                log.info("Nội dung tin nhắn không khớp với regex pattern.");
                constraintValidatorContext.disableDefaultConstraintViolation();
                constraintValidatorContext.buildConstraintViolationWithTemplate("Nội dung tin nhắn không khớp với template.")
                        .addConstraintViolation();
                return false;
            }
        } catch (PatternSyntaxException e) {
            log.error("Lỗi regex không hợp lệ cho templateId {}: {}", templateId, e.getMessage());
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate("Regex pattern không hợp lệ cho templateId " + templateId)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
