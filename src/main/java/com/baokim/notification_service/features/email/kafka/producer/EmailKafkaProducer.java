package com.baokim.notification_service.features.email.kafka.producer;

import com.baokim.notification_service.features.email.dtos.kafka.EmailKafkaMessage;
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
public class EmailKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topic.email}")
    private String emailTopic;

    public void sendToKafka(Long idEmail, String requestId) {
        try {
            EmailKafkaMessage message = new EmailKafkaMessage();

            message.setEmailId(idEmail);
            message.setRequestId(requestId);

            String json = objectMapper.writeValueAsString(message);

            kafkaTemplate.send(emailTopic, requestId, json);

        } catch (JsonProcessingException e) {
            log.error("[Kafka] Lỗi khi convert Email message sang JSON requestId: {}", requestId, e);
        }
    }
}
