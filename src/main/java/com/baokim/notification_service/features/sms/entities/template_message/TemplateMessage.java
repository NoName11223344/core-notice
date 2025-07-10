package com.baokim.notification_service.features.sms.entities.template_message;

import com.baokim.notification_service.bases.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "message_templates")
@Data
public class TemplateMessage extends BaseEntity {

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "category_id")
    private Integer categoryId;

    @Lob
    @Column(name = "sample_text")
    private String sampleText;

    @Lob
    @Column(name = "regex_pattern")
    private String regexPattern;

    @Column(name = "status")
    private Byte status;

    @Column(name = "merchant_code")
    private String merchantCode;

    @Column(name = "note")
    private String note;

}
