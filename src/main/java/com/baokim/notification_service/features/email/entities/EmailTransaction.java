package com.baokim.notification_service.features.email.entities;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "email_transactions")
public class EmailTransaction extends BaseEntity {

    private Byte status; // 0: chưa gửi, 2: đã gửi, 3: lỗi

    @Column(name = "content_type")
    private Byte contentType; // 1: HTML, 2: Text

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "email_from")
    private String emailFrom;

    @Column(name = "email_from_name")
    private String emailFromName;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "provider")
    private String provider;

    @Column(name = "retry_count")
    private Byte retryCount;

    // Trường này sẽ lưu danh sách file JSON: [{"fileName": "...", "filePath": "..."}]
    @Column(columnDefinition = "TEXT")
    private String attachments;

    @Column(name = "merchant_code", length = 50)
    private String merchantCode;
}