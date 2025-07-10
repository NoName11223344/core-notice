package com.baokim.notification_service.features.email.entities;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "email_recipients")
public class EmailRecipient extends BaseEntity {

    @Column(name = "email_id")
    private Long emailId;

    @Column(name = "email_type")
    private Byte emailType; // 1: to 2 la cc

    @Column(name = "email_address")
    private String emailAddress;

}
