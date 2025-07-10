package com.baokim.notification_service.features.sms.dtos.responses.vmg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SendMessage {
    private String to;
    private String telco;
    private int type;
    private String from;
    private String message;
    private String scheduled;
    private String requestId;
    private int useUnicode;
}
