package com.baokim.notification_service.features.sms.kafka.producer;

import com.baokim.notification_service.features.sms.dtos.kafka.SmsKafkaMessage;
import com.baokim.notification_service.features.sms.dtos.requests.sms.BaseSmsSendRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SmsKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.sms}")
    private String smsTopic;

    public void sendToKafka(BaseSmsSendRequest request, String tranId) {
        try {
            SmsKafkaMessage message = new SmsKafkaMessage();

            message.setRequest(request);
            message.setTranId(tranId);

            String json = objectMapper.writeValueAsString(message);

            kafkaTemplate.send(smsTopic, tranId, json);

            log.info("[Kafka] Đã gửi SMS message tới topic: {}, requestId: {}", smsTopic, request.getRequestId());

        } catch (JsonProcessingException e) {
            log.error("[Kafka] Lỗi khi convert SMS message sang JSON requestId: {}", request.getRequestId(), e);
        }
    }
}
