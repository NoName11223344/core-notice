package com.baokim.notification_service.features.sms.entities.fee;

import com.baokim.notification_service.bases.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "temp_fee_details")
public class TemFeeDetail extends BaseEntity {

    @Column(name = "temp_code", nullable = false, length = 50)
    private String tempCode;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "mobile_network", nullable = false, length = 50)
    private String mobileNetwork;

    @Column(name = "telco", nullable = false, length = 50)
    private String telco;

    @Column(name = "type", nullable = false)
    private Byte type;

    @Column(name = "fee", nullable = false)
    private Integer fee;

    @Column(name = "status", nullable = false)
    private Byte status;
}
