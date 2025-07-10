package com.baokim.notification_service.features.email.repositories;

import com.baokim.notification_service.features.email.entities.EmailTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailTransactionRepository extends JpaRepository<EmailTransaction, Long> {

    Optional<EmailTransaction> findByIdAndStatus(Long id, Byte status);
}
