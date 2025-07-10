package com.baokim.notification_service.features.sms.validators.message;

import com.baokim.notification_service.commons.utils.TextUtil;
import com.baokim.notification_service.features.sms.constants.BrandNameConstant;
import com.baokim.notification_service.features.sms.entities.blocked_keyword.BlockedKeyword;
import com.baokim.notification_service.features.sms.repositories.blocked_keyword.BlockedKeywordRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class BlockedMessageValidator implements ConstraintValidator<CheckBlockedMessage, String> {

    private final BlockedKeywordRepository blockedKeywordRepository;


    @Override
    public boolean isValid(String message, ConstraintValidatorContext constraintValidatorContext) {
        List<BlockedKeyword> blockedKeywords = blockedKeywordRepository.findByStatus(BrandNameConstant.STATUS_ACTIVE);

        if (blockedKeywords.isEmpty()) {
            return true;
        }
        String messageRemoveUnicode = TextUtil.removeUnicode(message);

        for (BlockedKeyword blockedKeyword : blockedKeywords) {
            String keywordRemoveUnicode = TextUtil.removeUnicode(blockedKeyword.getKeyword());
            if (messageRemoveUnicode.contains(keywordRemoveUnicode)){
                log.info("Nội dung tin nhắn {} chưa từ khoá {} không hợp lệ" , message, blockedKeyword.getKeyword());
                return false;
            }
        }
        return true;
    }
}
