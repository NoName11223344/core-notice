package com.baokim.notification_service.features.sms.entities.merchant;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "merchants")
public class Merchant extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "merchant_code", nullable = false, length = 50, unique = true)
    private String merchantCode;

    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;

    @Column(name = "temp_fee_code", length = 255)
    private String tempFeeCode;

    @Column(name = "status", nullable = false)
    private Byte status;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "from_email", length = 255)
    private String fromEmail;

    @Column(name = "is_sent_sms", nullable = false)
    private Byte isSentSms;

    @Column(name = "is_sent_email", nullable = false)
    private Byte isSentEmail;

    @Column(name = "sms_limit_in_day")
    private Integer smsLimitInDay;
}
