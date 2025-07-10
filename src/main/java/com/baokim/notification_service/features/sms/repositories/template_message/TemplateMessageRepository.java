package com.baokim.notification_service.features.sms.repositories.template_message;

import com.baokim.notification_service.features.sms.entities.template_message.TemplateMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TemplateMessageRepository extends JpaRepository<TemplateMessage, String> {
    Optional<TemplateMessage> findByIdAndMerchantCodeAndBrandNameAndStatus(Long id, String merchantCode, String brandName, Byte status);
}
