package com.baokim.notification_service.features.sms.dtos.responses.vmg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SmsApiResponse {

    public final static String SUCCESS_CODE = "000";
    public final static String FAIL = "11";

    private SendMessage sendMessage;
    private int msgLength;
    private Byte mtCount;
    private String account;
    private String errorCode;
    private String errorMessage;
    private String referentId;

    public static boolean isSmsSendSuccess(String responseCode) {
        return SUCCESS_CODE.equals(responseCode);
    }
}

