package com.baokim.notification_service.features.email.kafka.consumer;

import com.baokim.notification_service.features.email.dtos.kafka.EmailKafkaMessage;
import com.baokim.notification_service.features.email.services.EmailProviderFactory;
import com.baokim.notification_service.features.email.services.strategy.EmailProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailKafkaConsumer {
    private final ObjectMapper objectMapper;
    private final EmailProviderFactory emailProviderFactory;

    @KafkaListener(topics = "${spring.kafka.topic.email}", groupId = "${spring.kafka.consumer.group-id-email}")
    public void consumeSms(String jsonMessage) {
        try {

            EmailKafkaMessage kafkaMessage = objectMapper.readValue(jsonMessage, EmailKafkaMessage.class);

            log.info("[Kafka - consumer] Nhận Email thành công với requestId {}", kafkaMessage.getRequestId());

            EmailProvider emailProvider = emailProviderFactory.getProvider();

            emailProvider.sendEmail(kafkaMessage.getEmailId());

            log.info("[Kafka - consumer] Xử lý Email thành công với requestId: {}", kafkaMessage.getRequestId());

        } catch (Exception e) {
            log.error("[Kafka- consumer] Lỗi xử lý Email từ Kafka: {}", e.getMessage(), e);
        }
    }
}
