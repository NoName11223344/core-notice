package com.baokim.notification_service.features.sms.entities.sms;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sms_transactions")
public class SmsTransaction extends BaseEntity {

    @Column(name = "trans_id")
    private String transId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "reference_id_partner")
    private String referenceIdPartner;

    @Column(name = "reference_id_provider")
    private String referenceIdProvider;

    @Column(name = "provider_code")
    private String providerCode;

    @Column(name = "brandname")
    private String brandname;

    @Column(name = "sent_type")
    private Byte sentType;

    @Column(name = "sms_type")
    private Byte smsType;

    @Column(name = "scheduled")
    private LocalDateTime scheduled;

    @Column(name = "telco")
    private String telco;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "status")
    private Byte status;

    @Column(name = "template_id")
    private Long templateId;
}
