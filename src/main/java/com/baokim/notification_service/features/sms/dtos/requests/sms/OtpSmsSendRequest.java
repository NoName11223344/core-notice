package com.baokim.notification_service.features.sms.dtos.requests.sms;

import com.baokim.notification_service.features.sms.constants.SmsConstant;
import lombok.Getter;

@Getter
public class OtpSmsSendRequest extends BaseSmsSendRequest {

    public OtpSmsSendRequest() {
        this.operation = SmsConstant.OPERATION_OTP;
    }
}
