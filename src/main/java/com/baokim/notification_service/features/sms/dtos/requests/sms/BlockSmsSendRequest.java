package com.baokim.notification_service.features.sms.dtos.requests.sms;


import com.baokim.notification_service.features.sms.constants.SmsConstant;
import lombok.Getter;

@Getter
public class BlockSmsSendRequest extends BaseSmsSendRequest {
    public BlockSmsSendRequest() {
        this.operation = SmsConstant.OPERATION_BLOCK;
    }
}
