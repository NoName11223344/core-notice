package com.baokim.notification_service.features.sms.dtos.requests.vmg;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SentOtpVmgRequest {
    private String requestId;
    private String to;
    private Byte type;
    private String from;
    private String message;
    private String scheduled = "";
    private Byte useUnicode;
}
