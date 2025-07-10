package com.baokim.notification_service.features.sms.services.sms.external;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;

public interface SmsProvider {
    String sendOtp(BaseSmsSendRequest sentOtpRequest);

    String sendBlockSms(BaseSmsSendRequest sentBlockSmsRequest);

    String sendCskhSms(BaseSmsSendRequest sentCskhSmsRequest);
}
