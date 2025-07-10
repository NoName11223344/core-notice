package com.baokim.notification_service.features.sms.entities.brandname;

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
@Table(name = "brandnames")
public class BrandName extends BaseEntity {

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "status")
    private Byte status;
}
