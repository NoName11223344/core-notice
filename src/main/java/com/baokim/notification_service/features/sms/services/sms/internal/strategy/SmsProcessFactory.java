package com.baokim.notification_service.features.sms.services.sms.internal.strategy;

import com.baokim.notification_service.features.sms.constants.SmsConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SmsProcessFactory {
    private final BlockSmsProcessStrategy blockSmsProcessStrategy;
    private final OtpSmsProcessStrategy otpSmsProcessStrategy;
    private final CskhSmsProcessStrategy cskhSmsProcessStrategy;

    public SmsProcessStrategy getSmsStrategy(int operation) {
        switch (operation) {
            case SmsConstant.OPERATION_CSKH:
                return cskhSmsProcessStrategy;
            case SmsConstant.OPERATION_OTP:
                return otpSmsProcessStrategy;
            case SmsConstant.OPERATION_BLOCK:
                return blockSmsProcessStrategy;
            default:
                throw new IllegalArgumentException("Mã operation không hợp lệ: " + operation);
        }
    }

}
