package com.baokim.notification_service.features.email.constants;

public class EmailConstant {
    public static final String PROVIDER_SENDGRID = "sendgrid";
    public static final String PROVIDER_MAILGUN = "mailgun";

    public static final Byte HTML = 1;
    public static final Byte TEXT = 2;
    public static final Byte DEFAULT_RETRY = 0;

    public static final byte SENT = 1;           // Đã gửi
    public static final byte PENDING = 2;        // Chưa gửi
    public static final byte FAILED = 3;

    public static final byte EMAIL_TO = 1;
    public static final byte EMAIL_CC = 2;




}
