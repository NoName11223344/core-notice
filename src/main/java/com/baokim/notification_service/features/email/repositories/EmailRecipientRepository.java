package com.baokim.notification_service.features.email.repositories;

import com.baokim.notification_service.features.email.entities.EmailRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmailRecipientRepository  extends JpaRepository<EmailRecipient, Long> {
    List<EmailRecipient> getEmailsRecipientByEmailId(Long emailId);

}
