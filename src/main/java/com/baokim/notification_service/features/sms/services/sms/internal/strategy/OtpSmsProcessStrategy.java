package com.baokim.notification_service.features.sms.services.sms.internal.strategy;

import com.baokim.notification_service.commons.utils.EncryptionUtil;
import com.baokim.notification_service.features.sms.configs.SmsConfig;
import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransaction;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransactionDetail;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionDetailRepository;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionRepository;
import com.baokim.notification_service.features.sms.services.sms.external.SmsProvider;
import com.baokim.notification_service.features.sms.services.sms.external.SmsProviderFactory;
import com.baokim.notification_service.features.sms.services.sms.internal.SmsTransactionCommonService;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
import com.baokim.notification_service.commons.utils.HelperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OtpSmsProcessStrategy implements SmsProcessStrategy {
    private final SmsConfig smsConfig;
    private final SmsTransactionRepository smsTransactionRepository;
    private final SmsTransactionDetailRepository smsTransactionDetailRepository;
    private final SmsProviderFactory smsProviderFactory;
    private final SmsTransactionCommonService smsTransactionCommonService;

    @Override
    public SmsTransaction createSmsTransaction(BaseSmsSendRequest request, String tranId) {

        SmsTransaction smsTransaction = SmsTransaction.builder()
                .transId(tranId)
                .requestId(request.getRequestId())
                .requestTime(DateTimeUtil.convertLocalDateTime(request.getRequestTime()))
                .referenceIdPartner(request.getReferenceIdPartner())
                .merchantCode(request.getMerchantCode())
                .brandname(request.getFrom())
                .providerCode(smsConfig.getProvider().toUpperCase())
                .sentType(SmsConstant.SENT_TO_ONE)
                .smsType(SmsConstant.SMS_TYPE_OTP)
                .scheduled(request.getScheduled() != null ? DateTimeUtil.convertLocalDateTime(request.getScheduled()) : null)
                .templateId(request.getTemplateId())
                .status(SmsConstant.SENT_MESS_PENDING)
                .build();

        return smsTransactionRepository.save(smsTransaction);
    }

    @Override
    public SmsTransactionDetail createSmsTransactionDetail(BaseSmsSendRequest request, String tranId) {

        SmsTransactionDetail smsTransactionDetail = SmsTransactionDetail.builder()
                .transId(tranId)
                .sentTo(HelperUtil.formatHeadPhone(request.getTo()))
                .content(EncryptionUtil.encrypt(request.getMessage()))
                .merchantCode(request.getMerchantCode())
                .status(SmsConstant.SENT_MESS_PENDING)
                .build();

        return smsTransactionDetailRepository.save(smsTransactionDetail);
    }

    @Override
    public String sendSms(BaseSmsSendRequest request) {

        SmsProvider provider = smsProviderFactory.getProvider();

        return provider.sendOtp(request);
    }

    @Override
    public SmsTransaction updateSmsTransaction(String responseProvider, String tranId) {

       return smsTransactionCommonService.updateSmsTransaction(responseProvider, tranId);

    }

    @Override
    public SmsTransactionDetail updateSmsTransactionDetail(BaseSmsSendRequest request, String responseProvider, String tranId) {

        return smsTransactionCommonService.updateSmsTransactionDetail(request, responseProvider, tranId);

    }

}
