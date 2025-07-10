package com.baokim.notification_service.features.sms.dtos.requests.sms;


import com.baokim.notification_service.features.sms.constants.SmsConstant;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class CskhSmsSendRequest extends BaseSmsSendRequest {

    @Pattern(regexp = "^(?:01|02|04|05|07|08)?$", message = "Mã telco không hợp lệ")
    private String telco;

    public CskhSmsSendRequest() {
        this.operation = SmsConstant.OPERATION_CSKH;
    }
}
