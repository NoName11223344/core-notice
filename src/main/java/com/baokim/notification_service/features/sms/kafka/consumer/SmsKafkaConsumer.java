package com.baokim.notification_service.features.sms.kafka.consumer;

import com.baokim.notification_service.features.sms.dtos.kafka.SmsKafkaMessage;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.baokim.notification_service.features.sms.services.sms.internal.SmsAsyncService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class SmsKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final SmsAsyncService smsAsyncService;

    @KafkaListener(topics = "${spring.kafka.topic.sms}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeSms(String jsonMessage) {
        try {

            SmsKafkaMessage kafkaMessage = objectMapper.readValue(jsonMessage, SmsKafkaMessage.class);

            BaseSmsSendRequest request = kafkaMessage.getRequest();

            String tranId = kafkaMessage.getTranId();

            log.info("[Kafka - consumer] Nhận SMS thành công với requestId {}", request.getRequestId());

            smsAsyncService.processSendSmsAsync(request, tranId);

            log.info("[Kafka - consumer] Xử lý SMS thành công với requestId: {}", request.getRequestId());

        } catch (Exception e) {
            log.error("[Kafka- consumer] Lỗi xử lý SMS từ Kafka: {}", e.getMessage(), e);
        }
    }
}
