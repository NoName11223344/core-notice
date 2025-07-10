package com.baokim.notification_service.features.sms.constants;

public class SmsConstant {
    public static final String PROVIDER_VMG = "vmg";

    // Loại gửi tin nhắn
    public static final byte SENT_TO_ONE = 1;
    public static final byte SENT_TO_MANY = 2;

    // Loại SMS
    public static final byte SMS_TYPE_CUSTOMER_CARE = 1;
    public static final byte SMS_TYPE_ADV = 2;
    public static final byte SMS_TYPE_CUSTOMER_CARE_BLOCK = 3;
    public static final byte SMS_TYPE_OTP = 4;
    // Trạng thái gửi tibytehắn
    public static final byte SENT_MESS_SUCCESS = 1;
    public static final byte SENT_MESS_FAIL = 2;
    public static final byte SENT_MESS_PENDING = 3;
    public static final byte SENT_MESS_WAITING_APPROVE = 4;
    public static final byte SENT_MESS_APPROVE = 5;
    public static final byte SENT_MESS_REJECT = 6;

    public static final int OPERATION_OTP = 9003;
    public static final int OPERATION_BLOCK = 9002;
    public static final int OPERATION_CSKH = 9000;

}

