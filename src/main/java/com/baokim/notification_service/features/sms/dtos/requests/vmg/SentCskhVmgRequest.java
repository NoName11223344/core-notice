package com.baokim.notification_service.features.sms.dtos.requests.vmg;

import lombok.Data;

@Data
public class SentCskhVmgRequest {
    private String requestId;
    private String to;
    private Byte type;
    private String from;
    private String message;
    private String scheduled = "";
    private String telco = "";
    private Byte useUnicode;
}
