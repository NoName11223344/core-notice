package com.baokim.notification_service.features.sms.services.sms.internal;

import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.services.sms.internal.strategy.SmsProcessFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsAsyncService {
    private final SmsProcessFactory strategyFactory;

    @Async
    @Transactional
    public void processSendSmsAsync(BaseSmsSendRequest request, String tranId) {
        try {

            SmsService.processSentSms(request, tranId, strategyFactory);

            log.info("Xử lý SMS băng Async thành công cho requestId {}", request.getRequestId());

        } catch (Exception e) {
            log.error(" Xử lý SMS băng Async thất bại : {}", e.getMessage(), e);
        }
    }
}
