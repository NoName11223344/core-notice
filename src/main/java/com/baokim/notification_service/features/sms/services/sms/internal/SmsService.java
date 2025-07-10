package com.baokim.notification_service.features.sms.services.sms.internal;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.bases.responses.ApiResponse;
import com.baokim.notification_service.features.sms.kafka.producer.SmsKafkaProducer;
import com.baokim.notification_service.features.sms.services.request_log.RequestLogService;
import com.baokim.notification_service.features.sms.services.sms.internal.strategy.SmsProcessFactory;
import com.baokim.notification_service.features.sms.services.sms.internal.strategy.SmsProcessStrategy;
import com.baokim.notification_service.commons.utils.BkTransactionUtil;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor // tránh viết thêm ở constructor khởi tạo nữa đỡ dài
public class SmsService {

    private final ApiResponse apiResponse;
    private final RequestLogService requestLogService;
    private final SmsKafkaProducer smsKafkaProducer;
    private final SmsAsyncService smsAsyncService;
    private final SmsProcessFactory strategyFactory;

    public ApiResponse sendSms(BaseSmsSendRequest request) {

        String tranId = BkTransactionUtil.generateBkTransId(BkTransactionUtil.LENGTH);

        try {
            smsKafkaProducer.sendToKafka(request, tranId);

            log.info("Đẩy Kafka từ Producer SMS thành công đối với requestId: {}", request.getRequestId());

        } catch (Exception e) {

            log.warn("Kafka Producer SMS lỗi, gọi bằng async luôn với requestId ({}): {}", request.getRequestId(), e.getMessage());

            smsAsyncService.processSendSmsAsync(request, tranId);
        }

        requestLogService.saveRequestLogSmsFromMerchant(request, apiResponse);

        return apiResponse;
    }

    static void processSentSms(BaseSmsSendRequest request, String tranId, SmsProcessFactory strategyFactory) {

        SmsProcessStrategy smsStrategy = strategyFactory.getSmsStrategy(request.getOperation());

        smsStrategy.createSmsTransaction(request, tranId);

        smsStrategy.createSmsTransactionDetail(request, tranId);

        String jsonResponse = smsStrategy.sendSms(request);

        smsStrategy.updateSmsTransaction(jsonResponse, tranId);

        smsStrategy.updateSmsTransactionDetail(request, jsonResponse, tranId);
    }

}
