package com.baokim.notification_service.features.sms.services.sms.internal;

import com.baokim.notification_service.features.sms.constants.SmsConstant;
import com.baokim.notification_service.features.sms.dtos.requests.sms.ProviderVmgRequest;
import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransaction;
import com.baokim.notification_service.features.sms.entities.sms.SmsTransactionDetail;
import com.baokim.notification_service.commons.exceptions.AppException;
import com.baokim.notification_service.commons.exceptions.ErrorCode;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionDetailRepository;
import com.baokim.notification_service.features.sms.repositories.sms.SmsTransactionRepository;
import com.baokim.notification_service.features.sms.services.request_log.RequestLogService;
import com.baokim.notification_service.commons.utils.DateTimeUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class WebhookService {

    private final ApiResponse apiResponse;
    private final SmsTransactionRepository smsTransactionRepository;
    private final SmsTransactionDetailRepository smsTransactionDetailRepository;
    private final RequestLogService requestLogService;
    private final ObjectMapper objectMapper;

    private static final Map<Integer, Byte> STATUS_VMG_MESSAGE_MAP = Map.of(
            -2, SmsConstant.SENT_MESS_FAIL,
            -1, SmsConstant.SENT_MESS_REJECT,
            0, SmsConstant.SENT_MESS_WAITING_APPROVE,
            1, SmsConstant.SENT_MESS_APPROVE,
            2, SmsConstant.SENT_MESS_SUCCESS
    );

    @Transactional
    public ApiResponse updateStatus(ProviderVmgRequest request) {

        try {
            log.info("Request updating status for VMG {}", request);
            SmsTransactionDetail smsTransactionDetail = smsTransactionDetailRepository
                    .findBySubReferenceId(request.getReferentId())
                    .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND));

            SmsTransaction smsTransaction = smsTransactionRepository.findByTransId(smsTransactionDetail.getTransId())
                    .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND));

            Byte status = STATUS_VMG_MESSAGE_MAP.get(request.getStatus());
            smsTransaction.setStatus(status);
            smsTransactionDetail.setStatus(status);

            smsTransactionRepository.save(smsTransaction);
            smsTransactionDetailRepository.save(smsTransactionDetail);

            requestLogService.saveRequestLogSmsFromProvider(
                    request,
                    objectMapper.writeValueAsString(apiResponse),
                    request.getRequestId(),
                    DateTimeUtil.getCurrentDateTime(),
                    SmsConstant.PROVIDER_VMG.toUpperCase()
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return apiResponse;
    }
}
