package com.baokim.notification_service.features.sms.services.sms.internal.strategy;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransaction;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransactionDetail;

public interface SmsProcessStrategy {
    public SmsTransaction createSmsTransaction(BaseSmsSendRequest baseSmsSendRequest, String tranId);

    public SmsTransactionDetail createSmsTransactionDetail(BaseSmsSendRequest baseSmsSendRequest, String tranId);

    public String sendSms(BaseSmsSendRequest request);

    public SmsTransaction updateSmsTransaction(String responseProvider, String tranId);

    public SmsTransactionDetail updateSmsTransactionDetail(BaseSmsSendRequest request, String responseProvider, String tranId);
}
